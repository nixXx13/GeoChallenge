package GameDispatcher;

import Common.GameData;
import Common.GameStage;
import Common.GameType;
import GameExceptions.GameException;
import GameManager.GameManagerFactory;
import GameManager.IGameManager;
import Player.IPlayer;
import QuestionsProvider.IQuestionProvider;
import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameDispatcherImplTest {

    // player wishes to join a room that doesnt exist

    @Test
    void joinRoomThatDoesntExistTest(){

//        new MockUp<GameManagerFactory>() {
//            @Mock
//            public IGameManager getGameManager(GameRoom gameRoom, List<GameStage> gameStages){
//                System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
//                IGameManager gm = mock(IGameManager.class);
//                return gm;
//            }
//        };

        GameConfigImpl gameRoomConfig =  new GameConfigImpl("player","roomName",false,
                1,5, GameType.GameTypeEnum.TEST);

        IPlayer player = new PlayerMock(0);


        GameDispatcherImpl gameDispatcher = new GameDispatcherImpl(getQuestionsProvidersMock());
        gameDispatcher.dispatch(player,gameRoomConfig);

        assertEquals(((PlayerMock) player).getErrorList().get(0),"Room 'roomName' doesn't exists");
    }

    // more players join a room than its capacity
    @Test
    void roomFull(){
        GameConfigImpl gameRoomConfig =  new GameConfigImpl( "player2","roomName",true,
                1,5, GameType.GameTypeEnum.TEST);

        IPlayer player = new PlayerMock(0);
        IPlayer player1 = new PlayerMock(1);

        Map<GameType.GameTypeEnum,IQuestionProvider> qps = getQuestionsProvidersMock();
        GameDispatcherImpl gameDispatcher = new GameDispatcherImpl(qps);
        gameDispatcher.dispatch(player,gameRoomConfig);
        gameDispatcher.dispatch(player1,gameRoomConfig);

    }


    @Test
    void creatingRoomThatExists(){

    }

    class PlayerMock implements IPlayer{

        private int id;

        public PlayerMock(int id){
            this.id = id;
        }

        private List<String> errorList = new ArrayList<>();

        @Override
        public float getScore() {
            return 0;
        }

        @Override
        public String getName() {
            return "player" + id;
        }

        @Override
        public PlayerStatus getStatus() {
            return null;
        }

        @Override
        public void init(IGameManager gameManager, List<GameStage> gameStages) {

        }

        @Override
        public void ack(String info) {

        }

        @Override
        public void update(String msg) {

        }

        @Override
        public void grade(float newGrade) {

        }

        @Override
        public void end(String msg) {
        }

        public List<String> getErrorList(){
            return errorList;
        }

        @Override
        public void disconnect() {

        }

        @Override
        public void error(String errorMsg) {
            errorList.add(errorMsg);
        }

        @Override
        public void handleResponse(GameData gameData) {

        }

        @Override
        public void run() {

        }
    }

    private Map<GameType.GameTypeEnum,IQuestionProvider> getQuestionsProvidersMock(){
        IQuestionProvider qp = mock(IQuestionProvider.class);
        when(qp.getQuestions(5)).thenReturn(new ArrayList<>());

        Map<GameType.GameTypeEnum,IQuestionProvider> qps = new HashMap<>();
        qps.put(GameType.GameTypeEnum.TEST,qp);
        return qps;
    }
}