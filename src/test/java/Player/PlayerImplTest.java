package Player;

import Common.GameData;
import Common.GameStage;
import Common.INetworkConnector;
import Common.NetworkConnectorImpl;
import GameManager.IGameManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PlayerImplTest {

    @Test
    void gradeAndStatusTest() {
        INetworkConnector nc = getNetworkConnectorMock();
        List<GameStage> gameStages = getGameStages(2);

        int id  = 0;
        String name = "malvo";
        PlayerImpl player = new PlayerImpl(id,name,nc);

        IGameManager gameManager = null;

        player.init(gameManager,gameStages);
        player.grade(1f);
        assertEquals(IPlayer.PlayerStatus.ACTIVE, player.getStatus());
        player.grade(1f);
        assertEquals(IPlayer.PlayerStatus.FINISHED, player.getStatus());
    }

    @Test
    void handleAnswerTest() {
        INetworkConnector nc = getNetworkConnectorMock();
        List<GameStage> gameStages = getGameStages(2);

        int id  = 0;
        String name = "lorne";
        PlayerImpl player = new PlayerImpl(id,name,nc);

        GameManagerMock gameManager = new GameManagerMock();

        player.init(gameManager,gameStages);

        player.handleResponse(getAnswerGameData("a1",1f));
        player.grade(1f);
        player.handleResponse(getAnswerGameData("a2",1.5f));
        player.grade(2f);

        // third answer when there are 2 question
        player.handleResponse(getAnswerGameData("a3",1f));

        assertEquals(3f,player.getScore());
        assertEquals(2,gameManager.getAnswers().size());
        assertEquals(2,gameManager.getAnswersTime().size());
        assertEquals("a1",gameManager.getAnswers().get(0));
        assertEquals("a2",gameManager.getAnswers().get(1));
        assertEquals(1f,gameManager.getAnswersTime().get(0));
        assertEquals(1.5f,gameManager.getAnswersTime().get(1));
    }

    @Test
    void errorReadingServerDisconnectIdTest() {
        INetworkConnector nc = getNetworkConnectorMock();
        when(nc.read()).thenReturn(null);
        when(nc.send(any(List.class))).thenReturn(true);

        int id  = 100;
        String name = "solverson";
        PlayerImpl player = new PlayerImpl(id,name,nc);

        GameManagerMock gameManager = new GameManagerMock();

        player.init(gameManager,null);
        player.run();

        assertEquals(id,gameManager.getDisconnectId());
    }

    private static INetworkConnector getNetworkConnectorMock(){
        NetworkConnectorImpl nc = mock(NetworkConnectorImpl.class);
        when(nc.init()).thenReturn(true);
        when(nc.send(any(GameData.class))).thenReturn(true);
        return nc;
    }

    private static List<GameStage> getGameStages(int questionNumber) {
        List<GameStage> gameStages = new ArrayList<>();
        for (int i = 0; i < questionNumber; i++) {
            gameStages.add(new GameStage("q"+i, null, "a"+i));
        }
        return gameStages;
    }

    class GameManagerMock implements IGameManager{

        private List<String> answers;
        private List<Float> answersTime;
        private int disconnectId;
        public GameManagerMock(){
            answers = new ArrayList<>();
            answersTime = new ArrayList<>();
            disconnectId = -1;
        }

        @Override
        public void startGame() {

        }

        @Override
        public void receiveAnswer(int currPlayerId, GameStage gameStage, String answer, float time) {
            answers.add(answer);
            answersTime.add(time);
        }

        @Override
        public void receiveDisconnect(int playerId) {
            disconnectId = playerId;
        }

        public List<String> getAnswers(){
            return answers;
        }

        public List<Float> getAnswersTime(){
            return answersTime;
        }

        public int getDisconnectId() {
            return disconnectId;
        }
    }

    private GameData getAnswerGameData(String answer, Float time){
        Map<String,String> map = new HashMap<>();
        map.put("time",String.format("%.2f",time));
        map.put("answer",answer);
        return new GameData(GameData.GameDataType.DATA,map);
    }

}