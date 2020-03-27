import Common.GameData;
import Common.GameType;
import Common.INetworkConnector;
import Common.NetworkConnectorImpl;
import GameDispatcher.GameConfigImpl;
import GameDispatcher.GameDispatcherImpl;
import Player.IPlayer;
import Player.PlayerFactory;
import QuestionsProvider.IQuestionProvider;
import QuestionsProvider.QuestionProviderFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Main {

    private final static Logger logger = Logger.getLogger(Main.class);
    private final static int PORT = 4567;

    // TODO see below
    // create String consts for classes

    // create system properties file for port etc
    // lambda to send server ip

    public static void main(String[] args) {

        Map<GameType.GameTypeEnum,IQuestionProvider> questionProviders = QuestionProviderFactory.getAll();
        GameDispatcherImpl gameDispatcher = new GameDispatcherImpl(questionProviders);

        int id = 0;

        try  (ServerSocket ss = new ServerSocket(PORT)){
            logger.info("Server is up!");
            boolean run = true;

            while (run) {
                Socket socket = ss.accept();
                NetworkConnectorImpl networkConnector = new NetworkConnectorImpl(socket);
                GameConfigImpl gameConfig = getConnectionConfig(networkConnector);
                IPlayer player = PlayerFactory.getPlayer(networkConnector,gameConfig);

                gameDispatcher.dispatch(player, gameConfig);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // todo - blocking!, open thread to wait for player INFO
    private static GameConfigImpl getConnectionConfig(INetworkConnector networkConnector){
        networkConnector.init();
        GameData info = networkConnector.read();
        // todo check null
        return toRoomConfig(info);
    }

    private static GameConfigImpl toRoomConfig(GameData gameData){
        String s = gameData.getContent().get("msg");
        String[] attr = s.split(":");
        String playerName = attr[0];
        String roomName = attr[1];
        boolean isCreate = Boolean.valueOf(attr[2]);
        int size = Integer.valueOf(attr[3]);
        int questionsNumber = Integer.valueOf(attr[4]);
        logger.debug(String.format("Got connection with the following preferences: roomName - %s, create room - %b, room size - %d, question number - %d",roomName,isCreate,size,questionsNumber));
        return new GameConfigImpl(playerName,roomName,isCreate,size,questionsNumber, GameType.GameTypeEnum.GEO);
    }

}
