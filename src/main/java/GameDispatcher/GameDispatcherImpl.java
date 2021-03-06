package GameDispatcher;

import Common.GameStage;
import GameConfig.IRoomConfig;
import GameExceptions.GameConfigException;
import GameExceptions.GameException;
import GameManager.GameManagerFactory;
import GameManager.IGameManager;
import Player.IPlayer;
import QuestionsProvider.IQuestionProvider;
import Common.GameType.GameTypeEnum;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static GameDispatcher.GameDispatcherUtils.isStringInList;

public class GameDispatcherImpl implements IGameDispacher{
    private final static Logger logger = Logger.getLogger(GameDispatcherImpl.class);

    private Map<GameTypeEnum, IQuestionProvider> questionProviders;

    private Map<String, GameRoom> pendingRooms;

    public GameDispatcherImpl(Map<GameTypeEnum, IQuestionProvider> questionProviders){
        this.questionProviders = questionProviders;
        pendingRooms = new HashMap<>();
    }

    public void dispatch(IPlayer player, IRoomConfig roomConfig){
        try {
            if (roomConfig.isCreate()) {
                createRoom(player, roomConfig);
            } else {
                joinRoom(player, roomConfig.getRoomName());
            }

        }catch (GameException g){
            String errorMsg = g.getMessage();
            player.error(errorMsg);
            player.disconnect();
        }
    }

    // TODO - rethink synchronization and possible bottlenecks
    // read about hash map and concurrency

    private synchronized void createRoom(IPlayer roomCreator, IRoomConfig roomConfig) throws GameException {
        String roomName = roomConfig.getRoomName();
        if (doesRoomExists(roomName)){
            String errorMsg = String.format("Room '%s' already exists",roomName);
            logger.warn(errorMsg);
            throw new GameConfigException(errorMsg);
        }
        logger.info(String.format("Creating room '%s' for '%s'",roomName, roomCreator.getName()));
        GameRoom room = new GameRoom(roomConfig);
        pendingRooms.put(roomName,room);
        joinRoom(roomCreator,roomName);
    }

    private void joinRoom(IPlayer player, String roomName) throws GameException {

        GameRoom gameRoom = pendingRooms.get(roomName);
        if (gameRoom == null){
            String errorMsg = String.format("Room '%s' doesn't exists",roomName);
            logger.warn(errorMsg);
            throw new GameConfigException(errorMsg);
        }

        // make sure two threads arent editing same room
        synchronized (gameRoom) {
            gameRoom.addPlayer(player);
            logger.debug(String.format("Player '%s' joined room '%s'",player.getName(),roomName));
            player.ack("entered " + roomName);
            if (gameRoom.isFull()) {
                // TODO - fetch question here instead of start game.if questions are empty. gameRoom.getPlayers , for each player player.error
                String playersNames = gameRoom.getPlayers().stream().map(IPlayer::getName).collect(Collectors.toList()).toString();
                logger.info(String.format("Room '%s' is now full. starting game with players '%s'",roomName,playersNames));
                startGame(gameRoom);
                pendingRooms.remove(gameRoom.getName());
            }
        }

    }

    private void startGame(GameRoom gameRoom){
        GameTypeEnum gameType = gameRoom.getType();
        IQuestionProvider questionProvider = questionProviders.get(gameType);
        List<GameStage> questions = questionProvider.getQuestions(gameRoom.getQuestionsNumber());

        IGameManager gameManager = GameManagerFactory.getGameManager(gameRoom, questions, gameType);
        gameManager.startGame();
    }

    private boolean doesRoomExists(String roomName){
        List<String> roomNames = new ArrayList<>(pendingRooms.keySet());
        return isStringInList(roomNames,roomName);
    }
}
