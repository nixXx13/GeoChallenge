import GameManager.GameManagerImpl;
import GameManager.IGameManager;
import Common.GameStage;
import Player.IPlayer;
import Player.PlayerFactory;
import QuestionsProvider.IQuestionProvider;
import QuestionsProvider.QuestionProviderFactory;
import QuestionsProvider.QuestionProviderFactory.QuestionProviderType;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    private final static int PLAYERS_NUM = 1;
    private final static int QUESTIONS_NUMBER = 5;
    private final static int PORT = 4567;
    private final static QuestionProviderType qType = QuestionProviderType.GEO_REMOTE;

    // TODO see below
    // implement game dispacher
    // create String consts for classes

    // create CI pipeline
    // create system properties file for port etc

    public static void main(String[] args) {

        try  (ServerSocket ss = new ServerSocket(PORT)){
            logger.info("Server is up!");
            boolean run = true;

            while (run) {
                int playersNum = PLAYERS_NUM;

                // TODO - throw dedicated exception?
                IQuestionProvider questionProvider = QuestionProviderFactory.
                        getQuestionProvider(qType);

                Map<Integer, IPlayer> players = new HashMap<>();

                for(int i=0;i<playersNum;i++) {
                    Socket socket = ss.accept();
                    PlayerFactory playerFactory = new PlayerFactory();

                    IPlayer player = playerFactory.getPlayer(socket, i);
                    player.ack("connected");
                    players.put(i, player);
                }

                IGameManager gameManager = new GameManagerImpl(players, questionProvider.getQuestions(QUESTIONS_NUMBER));
                gameManager.startGame();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
