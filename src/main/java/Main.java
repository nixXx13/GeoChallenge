import GameManager.GameManagerImpl;
import GameManager.IGameManager;
import Common.GameStage;
import Player.IPlayer;
import Player.PlayerFactory;
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

    // ---------------------
    // Temp  implementation
    // ---------------------

    // TODO see below
    // implement game dispacher
    // create String consts for classes

    // create CI pipeline
    // create Game Stages generator service

    // create system properties file for port etc

    public static void main(String[] args) {

//        ServerSocket ss;
//        try  {
        try  (ServerSocket ss = new ServerSocket(8888)){
//            ss = new ServerSocket(8888);
            logger.info("Server is up!");

            boolean run = true;

            while (run) {
                int id = 1;

                Socket socket = ss.accept();
                PlayerFactory playerFactory = new PlayerFactory();

                Map<Integer, IPlayer> players = new HashMap<>();

                IPlayer player1 = playerFactory.getPlayer(socket, id);

                players.put(id, player1);

                IGameManager gameManager = new GameManagerImpl(players, getGameStages());
                gameManager.startGame();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static List<GameStage> getGameStages(){
        ArrayList<GameStage> qs = new ArrayList<>();

        List<String> pAnswers = new ArrayList<>();
        pAnswers.add("1");
        pAnswers.add("2");
        GameStage gameStage1 = new GameStage("1+1",pAnswers,"2");
        GameStage gameStage2 = new GameStage("1+2",pAnswers,"3");

        qs.add(gameStage1);
        qs.add(gameStage2);

        return qs;
    }
}
