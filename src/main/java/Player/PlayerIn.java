package Player;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static Common.ConnectionUtils.readBufferReader;

public class PlayerIn {

    final static Logger logger = Logger.getLogger(PlayerIn.class);

    private InputStreamReader is;
    private IPlayer player;

    private String END = "end";

    public PlayerIn(InputStreamReader is){
        this.is = is;
    }

    public void listen() {
        logger.info(String.format("Listening to player '%d' input",player.getId()));
        try( BufferedReader br = new BufferedReader(is)){
            String playerInput = readBufferReader(br);
            while ( playerInput != null && !END.equals(playerInput)){
                player.handleAnswer(playerInput);
                playerInput = readBufferReader(br);
            }
        } catch (IOException e) {
            logger.error(String.format("Error listening to player '%d' input",player.getId()));
            e.printStackTrace();
            // TODO - notify GM finish/decrease
        }
        logger.info(String.format("Stopped listening to player '%d' input",player.getId()));
    }

    public void close(){
        try {
            is.close();
        } catch (IOException e) {
            logger.error(String.format("Failed closing Player '%d' InputStream",player.getId()));
        }
    }

    public void setPlayer(PlayerImpl player) {
        this.player = player;
    }




}
