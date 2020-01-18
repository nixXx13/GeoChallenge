package GameManager;

import Models.GameStage;
import Player.IPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerImplTest {

    // TODO - remove tests package from jar

    private GameManagerImpl gameManager;
    private HashMap<Integer,IPlayer> players;
    private List<GameStage> gameStages;

    private final String MSG_UPDATE = "Player %s scored %f.";
    private final String MSG_END    = "Player %s finished game.";


    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.Test
    void OnePlayerTest() throws InterruptedException {
        int playerId1 = 11;

        gameStages = new ArrayList<>();
        gameStages.add(new GameStage("q",null,"answer"));
        players = new HashMap<Integer, IPlayer>();
        players.put(playerId1,buildMockPlayer(playerId1,"answer", gameManager));

        gameManager = new GameManagerImpl(players,gameStages);

        gameManager.startGame();
        Thread.sleep(1000);
        assertEquals(1,players.get(playerId1).getScore());

    }

    @org.junit.jupiter.api.Test
    void TwoPlayersTest() throws InterruptedException {
        int playerId1 = 11;
        int playerId2 = 22;

        gameStages = new ArrayList<>();
        gameStages.add(new GameStage("q",null,"answer"));
        players = new HashMap<>();
        players.put(playerId1,buildMockPlayer(playerId1,"answer",gameManager));
        players.put(playerId2,buildMockPlayer(playerId2,"answer111",gameManager));

        gameManager = new GameManagerImpl(players,gameStages);

        gameManager.startGame();
        Thread.sleep(1000);
        assertEquals(1,players.get(playerId1).getScore());
        assertEquals(0,players.get(playerId2).getScore());

    }

    private IPlayer buildMockPlayer(final int playerId, final String answer, GameManagerImpl gameManager){

        // TODO - replace with mock
        class PlayerMock implements IPlayer{

            private IGameManager gameManager;

            private boolean validGameEnd;
            private boolean validGameUpdate;
            private float score;

            public PlayerMock(){
                score = 0f;
                validGameEnd = false;
                validGameUpdate = false;
            }

            @Override
            public void init(IGameManager gameManager, List<GameStage> gameStages) {
                System.out.println(String.format("player %d started",playerId));
                this.gameManager = gameManager;

            }

            @Override
            public void end(UpdateType type, String update) {
                System.out.println(String.format("[DEBUG} - received end from game manager - '%s'",update));

                String updateMsg = String.format(MSG_END,playerId);
                if (updateMsg.equals(update)) {
                    validGameEnd = true;
                    System.out.println(String.format("[DEBUG} - received end update is correct"));
                }
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
                validatePlayerRun();
                return score;
            }

            @Override
            public void addScore(float newGrade) {
                System.out.println("adding score " + newGrade);
                score+=newGrade;
            }


            @Override
            public void update(UpdateType type, String update) {
                System.out.println(String.format("[DEBUG} - received update of type %s from game manager - '%s'",type.toString(),update));

                String expecteddUpdateMsg = String.format(MSG_UPDATE,playerId,score);
                if ( type.equals(UpdateType.STATUS) && update.equals(expecteddUpdateMsg)){
                    validGameUpdate = true;
                    System.out.println(String.format("[DEBUG} - received status update is correct"));
                }

                if( type.equals(UpdateType.GRADE )){
                    score+=Float.valueOf(update);
                }
            }

            private void validatePlayerRun(){
//                assertTrue(validGameEnd,"");
//                assertTrue(validGameUpdate,"");
            }

            @Override
            public void run() {
                System.out.println("Thread "+ playerId+ " running");
                gameManager.receiveAnswer(playerId,answer,1);
            }
        }
        return new PlayerMock();
    }
}