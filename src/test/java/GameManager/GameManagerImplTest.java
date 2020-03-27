package GameManager;

import Common.GameData;
import Common.GameStage;
import Player.IPlayer;
import Player.PlayerImpl;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerImplTest {

    private final static Logger logger = Logger.getLogger(GameManagerImplTest.class);

    private GameManagerImpl gameManager;
    private HashMap<String,IPlayer> players;
    private List<GameStage> gameStages;

    private int endCount = 0;


    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @Test
    void OnePlayerTest() throws InterruptedException {
        logger.info("=========== Starting test OnePlayerTest");
        String playerId1 = "p1";

        gameStages = new ArrayList<>();
        gameStages.add(new GameStage("q",null,"answer"));
        players = new HashMap<String, IPlayer>();

        List<String> answers = new ArrayList<>();
        answers.add("answer");
        players.put(playerId1,new BasicPlayerMock(playerId1,gameStages,answers));

        gameManager = new GameManagerImpl(players,gameStages);

        gameManager.startGame();
        Thread.sleep(1000);
        assertEquals(1,players.get(playerId1).getScore());

    }

    @Test
    void TwoPlayersTest() throws InterruptedException {
        logger.info("=========== Starting test TwoPlayersTest");
        String playerId1 = "p1";
        String playerId2 = "p2";

        gameStages = new ArrayList<>();
        gameStages.add(new GameStage("q1",null,"answer1"));
        gameStages.add(new GameStage("q2",null,"answer2"));
        gameStages.add(new GameStage("q3",null,"answer3"));
        players = new HashMap<>();

        List<String> answers1 = new ArrayList<>();
        answers1.add("answer1");
        answers1.add("answer2");
        answers1.add("answer3");

        players.put(playerId1,new BasicPlayerMock(playerId1,gameStages,answers1));

        List<String> answers2 = new ArrayList<>();
        answers2.add("answer111");
        answers2.add("answer2");
        answers2.add("answer111");
        players.put(playerId2,new BasicPlayerMock(playerId2,gameStages,answers2));

        gameManager = new GameManagerImpl(players,gameStages);

        gameManager.startGame();
        Thread.sleep(1000);
        assertEquals(3,players.get(playerId1).getScore());
        assertEquals(1,players.get(playerId2).getScore());

    }

    @Test
    void manyQuestionsTest() throws InterruptedException {
        logger.info("=========== Starting test OnePlayerTest");
        String playerId1 = "p1";
        String playerId2 = "p2";
        int qNum = 50;

        gameStages = new ArrayList<>();
        List<String> answersGood = new ArrayList<>();
        List<String> answersBad = new ArrayList<>();

        int i = 0;
        while(i<qNum){
            gameStages.add(new GameStage("q" + i,null,"answer" + i));
            answersGood.add("answer" + i);
            answersBad.add("answer" + i+1);
            i++;
        }

        players = new HashMap<>();

        players.put(playerId1,new BasicPlayerMock(playerId1,gameStages,answersGood));
        players.put(playerId2,new BasicPlayerMock(playerId2,gameStages,answersBad));

        gameManager = new GameManagerImpl(players,gameStages);

        gameManager.startGame();
        Thread.sleep(7000);
        assertEquals(50,players.get(playerId1).getScore());
        assertEquals(0,players.get(playerId2).getScore());

    }

    @Test
    void manyPlayersTest() throws InterruptedException {
        logger.info("=========== manyPlayersTest");
        int qNum = 5;
        int playersNum = 50;

        gameStages = new ArrayList<>();
        List<String> answersGood = new ArrayList<>();

        int i = 0;
        while(i<qNum){
            gameStages.add(new GameStage("q" + i,null,"answer" + i));
            answersGood.add("answer" + i);
            i++;
        }

        players = new HashMap<>();

        for(i=0;i<playersNum;i++){
            players.put("player"+i,getVerifyEndMock("player"+i,answersGood));
        }
        gameManager = new GameManagerImpl(players,gameStages);

        gameManager.startGame();
        Thread.sleep(7000);
        for(i=0;i<playersNum;i++){
            assertEquals(5,players.get("player"+i).getScore());
        }
        assertEquals(50,endCount);


    }

    public IPlayer getVerifyEndMock(String playerId, List<String> answers){

        class VerifyEndPlayerMock extends BasicPlayerMock{

            public VerifyEndPlayerMock(String playerId, List<String> answers) {
                super(playerId,gameStages, answers);
            }
            @Override
            public void end(String update) {
                addCount();
            }
        }

        return new VerifyEndPlayerMock(playerId,answers);

    }

    class BasicPlayerMock implements IPlayer{

        private IGameManager gameManager;
        private int answerIndex = 0;
        private float score;
        private int questionAnswered = 0;
        private String playerId;
        private List<String> answers;
        private List<GameStage> gameStages;

        public BasicPlayerMock(String playerId, List<GameStage> gameStages,List<String> answers){
            score = 0f;
            this.playerId = playerId;
            this.answers = answers;
            this.gameStages = gameStages;
        }

        @Override
        public void init(IGameManager gameManager, List<GameStage> gameStages) {
            logger.debug(String.format("player %s started",playerId));
            this.gameManager = gameManager;

        }

        @Override
        public void ack(String info) {

        }


        @Override
        public void end(String update) {

        }

        @Override
        public void disconnect() {

        }

        @Override
        public void error(String errorMsg) {

        }

        @Override
        public void handleResponse(GameData gameData) {
        }

        @Override
        public float getScore() {
            return score;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public PlayerImpl.PlayerStatus getStatus() {
            if (gameStages.size() == questionAnswered){
                return PlayerImpl.PlayerStatus.FINISHED;
            }
            return PlayerImpl.PlayerStatus.ACTIVE;
        }

        @Override
        public void grade(float newGrade) {
            score+=newGrade;
            questionAnswered+=1;
            logger.debug(String.format("new score " + score));
        }


        @Override
        public void update(String update) {
//                logger.debug(String.format("received update from game manager - '%s'",update));
        }


        @Override
        public void run() {
            logger.debug(String.format("Thread "+ playerId+ " running"));
            while(answerIndex<answers.size()) {
                gameManager.receiveAnswer(playerId,gameStages.get(questionAnswered), answers.get(answerIndex), 1);
                answerIndex += 1;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void addCount(){
        endCount+=1;
    }
}