package GameDispatcher;

import Common.GameData;
import Common.GameStage;
import Common.GameType;

import GameConfig.GameConfigImpl;
import GameConfig.IRoomConfig;
import GameManager.IGameManager;
import Player.IPlayer;
import QuestionsProvider.IQuestionProvider;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameDispatcherImplTest {
    private final static Logger logger = Logger.getLogger(GameDispatcherImplTest.class);

    // player wishes to join a room that doesnt exist
    @Test
    void joinRoomThatDoesntExistTest(){

        GameConfigImpl gameRoomConfig =  new GameConfigImpl("player","roomName",false,
                1,5, GameType.GameTypeEnum.TEST);

        IPlayer player = new PlayerMock(0);


        GameDispatcherImpl gameDispatcher = new GameDispatcherImpl(getQuestionsProvidersMock());
        gameDispatcher.dispatch(player,gameRoomConfig);

        assertEquals(((PlayerMock) player).getErrorList().get(0),"Room 'roomName' doesn't exists");
    }

    // more players join a room than its capacity
    @Test
    void roomFull() throws InterruptedException {
        Map<GameType.GameTypeEnum,IQuestionProvider> qps = getQuestionsProvidersMock();
        GameDispatcherImpl gameDispatcher = new GameDispatcherImpl(qps);

        List<Thread> createPlayersThreads = new ArrayList<>();
        List<Thread> joinPlayersThreads = new ArrayList<>();
        List<PlayerMock> joinPlayers = new ArrayList<>();

        for(int i = 0; i<8; i++){
            String roomName = String.valueOf(i/8);
            String playerName = String.format("player%d_%s",i,roomName);
            boolean isCreate = i%8 == 0;

            System.out.println( playerName + "," +  roomName +"," + isCreate);
            GameConfigImpl gameConfig =  new GameConfigImpl( playerName,roomName,isCreate,
                    4,1, GameType.GameTypeEnum.TEST);
            PlayerMock player = new PlayerMock(i);
            PlayerThread pt = new PlayerThread(player,gameDispatcher,gameConfig);
            Thread t = new Thread(pt);
            if (isCreate){
                createPlayersThreads.add(t);
            }else{
                joinPlayersThreads.add(t);
                joinPlayers.add(player);
            }
        }

        for(Thread pt: createPlayersThreads){
            pt.start();
        }
        Thread.sleep(1000);
        for(Thread pt: joinPlayersThreads){
            pt.start();
        }
        Thread.sleep(1000);
        String ackMsg = "entered " + "0";
        int ackedPlayers = 0;
        for(PlayerMock pm: joinPlayers){
            if(pm.getAckList().size()>0 && pm.getAckList().get(0).equals(ackMsg)){
                ackedPlayers +=1;
            }
        }
        // not including creator
        assertEquals(3,ackedPlayers);

    }


    @Test
    void manyConcurrentRooms() throws InterruptedException {
        Map<GameType.GameTypeEnum,IQuestionProvider> qps = getQuestionsProvidersMock();
        GameDispatcherImpl gameDispatcher = new GameDispatcherImpl(qps);

        List<Thread> createPlayers = new ArrayList<>();
        List<Thread> joinPlayersThread = new ArrayList<>();
        List<PlayerMock> joinPlayers = new ArrayList<>();

        int roomsNumber = 1000;


        for(int i = 0; i<4*roomsNumber; i++){
            String roomName = String.valueOf(i/4);
            String playerName = String.format("player%d_%s",i,roomName);
            boolean isCreate = i%4 == 0;

            GameConfigImpl gameConfig =  new GameConfigImpl( playerName,roomName,isCreate,
                    4,1, GameType.GameTypeEnum.TEST);
            PlayerMock player = new PlayerMock(i);
            PlayerThread pt = new PlayerThread(player,gameDispatcher,gameConfig);
            Thread t = new Thread(pt);
            if (isCreate){
               createPlayers.add(t);
            }else{
                joinPlayersThread.add(t);
                joinPlayers.add(player);
            }
        }

        for(Thread pt: createPlayers){
            pt.start();
        }
        Thread.sleep(1000);
        for(Thread pt: joinPlayersThread){
            pt.start();
        }

        Thread.sleep(5*1000);
        Map<String,Integer> roomJoins = new HashMap<>();
        for(int k=0;k<roomsNumber;k++){
            roomJoins.put("entered "+k,0);
        }
        for(PlayerMock pm: joinPlayers){
            if(pm.getAckList().size()>0) {
                String ackMsg = pm.getAckList().get(0);
                if (!roomJoins.containsKey(ackMsg)) {
                    roomJoins.put(ackMsg, 1);
                } else {
                    int ackNum = roomJoins.get(ackMsg);
                    roomJoins.put(ackMsg, ackNum + 1);
                }
            }
        }
        // not including creator - 3 in each entry
        int actualRooms = roomJoins.keySet().size();
        System.out.println("actual rooms " +actualRooms);
        assertEquals(roomsNumber,actualRooms);
        for (String roomAck:roomJoins.keySet()){
            assertEquals(3,roomJoins.get(roomAck));
            System.out.println(roomAck + "count - 3");
        }

    }

    class PlayerThread implements Runnable{

        private GameDispatcherImpl gameDispatcher;
        private IPlayer player;
        private IRoomConfig roomConfig;

        public PlayerThread(IPlayer player, GameDispatcherImpl gameDispatcher, IRoomConfig roomConfig){
            this.gameDispatcher = gameDispatcher;
            this.player = player;
            this.roomConfig = roomConfig;
        }

        @Override
        public void run() {
            logger.debug(String.format("dispatcher in thread of player '%s'",player.getName()));
            gameDispatcher.dispatch(player,roomConfig);
        }
    }

    class PlayerMock implements IPlayer{

        private int id;

        public PlayerMock(int id){
            this.id = id;
        }

        private List<String> errorList = new ArrayList<>();
        private List<String> ackList = new ArrayList<>();

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
            ackList.add(info);
        }

        public List<String> getAckList(){
            return ackList;
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