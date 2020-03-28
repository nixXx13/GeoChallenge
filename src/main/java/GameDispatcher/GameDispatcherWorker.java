package GameDispatcher;

import Common.GameData;
import Common.GameType;
import Common.INetworkConnector;
import Common.NetworkConnectorImpl;
import GameConfig.GameConfigFactory;
import GameConfig.GameConfigImpl;
import GameExceptions.GameCommunicationException;
import GameExceptions.GameConfigException;
import GameExceptions.GameException;
import Player.IPlayer;
import Player.PlayerFactory;
import org.apache.log4j.Logger;

import java.net.Socket;

public class GameDispatcherWorker implements Runnable {

    private final static Logger logger = Logger.getLogger(GameDispatcherWorker.class);
    private Socket socket;
    private IGameDispacher gameDispatcher;
    private GameConfigFactory gameConfigFactory;

    public GameDispatcherWorker(IGameDispacher gameDispatcher, Socket socket){
        this.socket = socket;
        this.gameDispatcher = gameDispatcher;
        gameConfigFactory = new GameConfigFactory();
    }

    @Override
    public void run() {
        // TODO - thread might get stuck
        NetworkConnectorImpl networkConnector = new NetworkConnectorImpl(socket);
        try {
            GameConfigImpl gameConfig = getConnectionConfig(networkConnector);
            IPlayer player = PlayerFactory.getPlayer(networkConnector,gameConfig);
            gameDispatcher.dispatch(player, gameConfig);
        } catch (GameException e) {
            String errorMsg = e.getMessage();
            GameData gameData = new GameData(GameData.GameDataType.ERROR,errorMsg);
            networkConnector.send(gameData);
            networkConnector.terminate();
        }
    }

    private GameConfigImpl getConnectionConfig(INetworkConnector networkConnector) throws GameException {
        if(networkConnector.init()){
            GameData info = networkConnector.read();
            return toRoomConfig(info);
        }
        else{
            throw new GameCommunicationException("Error communicating with player");
        }
    }

    private GameConfigImpl toRoomConfig(GameData gameData) throws GameConfigException {
        try {
            String s = gameData.getContent().get("msg");
            String[] attr = s.split(":");
            String playerName = attr[0];
            String roomName = attr[1];
            boolean isCreate = Boolean.valueOf(attr[2]);
            int size = Integer.valueOf(attr[3]);
            int questionsNumber = Integer.valueOf(attr[4]);
            logger.debug(String.format("Got connection with the following preferences: roomName - %s, create room - %b, room size - %d, question number - %d", roomName, isCreate, size, questionsNumber));
            return gameConfigFactory.getGameConfig(playerName, roomName, isCreate, size, questionsNumber, GameType.GameTypeEnum.GEO);
        }catch (GameConfigException ge) {
            // input validation exceptions
            logger.error(ge.getMessage());
            throw ge;
        }catch (Exception e){
            // TODO - narrow exception
            // parsing exceptions
            logger.error(String.format("Failed parsing players configuration - %s",gameData));
            throw new GameConfigException("Player configuration given are invalid");
        }
    }
}