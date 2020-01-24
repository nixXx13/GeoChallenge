package GameManager;

import Common.GameStage;
import Player.IPlayer;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerImplTest {

    final static Logger logger = Logger.getLogger(GameManagerImplTest.class);

    private GameManagerImpl gameManager;
    private HashMap<Integer,IPlayer> players;
    private List<GameStage> gameStages;

    private final String MSG_UPDATE = "Player %s scored %f.";
    private final String MSG_END    = "Player %s finished game.";


    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @Test
    void OnePlayerTest() throws InterruptedException {
        logger.info("=========== Starting test OnePlayerTest");
        int playerId1 = 11;

        gameStages = new ArrayList<>();
        gameStages.add(new GameStage("q",null,"answer"));
        players = new HashMap<Integer, IPlayer>();

        List<String> answers = new ArrayList<>();
        answers.add("answer");
        players.put(playerId1,buildMockPlayer(playerId1,answers));

        gameManager = new GameManagerImpl(players,gameStages);

        gameManager.startGame();
        Thread.sleep(1000);
        assertEquals(1,players.get(playerId1).getScore());

    }

    @Test
    void TwoPlayersTest() throws InterruptedException {
        logger.info("=========== Starting test TwoPlayersTest");
        int playerId1 = 11;
        int playerId2 = 22;

        gameStages = new ArrayList<>();
        gameStages.add(new GameStage("q1",null,"answer1"));
        gameStages.add(new GameStage("q2",null,"answer2"));
        gameStages.add(new GameStage("q3",null,"answer3"));
        players = new HashMap<>();

        List<String> answers1 = new ArrayList<>();
        answers1.add("answer1");
        answers1.add("answer2");
        answers1.add("answer3");

        players.put(playerId1,buildMockPlayer(playerId1,answers1));

        List<String> answers2 = new ArrayList<>();
        answers2.add("answer111");
        answers2.add("answer2");
        answers2.add("answer111");
        players.put(playerId2,buildMockPlayer(playerId2,answers2));

        gameManager = new GameManagerImpl(players,gameStages);

        gameManager.startGame();
        Thread.sleep(1000);
        assertEquals(3,players.get(playerId1).getScore());
        assertEquals(1,players.get(playerId2).getScore());

    }


    private IPlayer buildMockPlayer(final int playerId, final List<String> answers){

        // TODO - replace with mock
        class PlayerMock implements IPlayer{

            private IGameManager gameManager;
            private int answerIndex = 0;
            private float score;
            private int questionAnswered = 0;

            public PlayerMock(){
                score = 0f;
            }

            @Override
            public void init(IGameManager gameManager, List<GameStage> gameStages) {
                logger.debug(String.format("player %d started",playerId));
                this.gameManager = gameManager;

            }


            @Override
            public void end(String update) {
//                logger.debug(String.format("received end from game manager - '%s'",update));

//                String updateMsg = String.format(MSG_END,playerId);
//                if (updateMsg.equals(update)) {
//                    logger.debug(String.format("received end update is correct"));
//                }
            }

            @Override
            public void handleAnswer(String playerAnswer) {
            }

            @Override
            public int getId() {
                return playerId;
            }

            @Override
            public float getScore() {
                return score;
            }

            @Override
            public int getQuestionsAnswered() {
                return  questionAnswered;
            }

            @Override
            public void setQuestionsAnswered(int questionsAnswered) {
                this.questionAnswered = questionsAnswered;
            }

            @Override
            public void grade(float newGrade) {
                score+=newGrade;
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
                    gameManager.receiveAnswer(playerId, answers.get(answerIndex), 1);
                    answerIndex += 1;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new PlayerMock();
    }
}