import Common.GameType;

import GameDispatcher.GameDispatcherImpl;
import GameDispatcher.GameDispatcherWorker;
import GameDispatcher.IGameDispacher;
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
        IGameDispacher gameDispatcher = new GameDispatcherImpl(questionProviders);

        try  (ServerSocket ss = new ServerSocket(PORT)){
            logger.info("Server is up!");
            boolean run = true;

            while (run) {
                Socket socket = ss.accept();
                GameDispatcherWorker gameDispatcherWorker = new GameDispatcherWorker(gameDispatcher,socket);
                Thread t = new Thread(gameDispatcherWorker);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
