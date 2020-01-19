package Player;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;

import static Common.ConnectionUtils.readBufferReader;

public class PlayerIn {

    final static Logger logger = Logger.getLogger(PlayerIn.class);

    private BufferedReader br;
    private IPlayer player;

    public PlayerIn(BufferedReader br){
        this.br = br;
    }

    public void listen() {
        String playerInput = readBufferReader(br);
        while ( playerInput != null){
            player.handleAnswer(playerInput);
            playerInput = readBufferReader(br);
        }
        // TODO - Player exited/finished/connection terminated

    }

    public void close(){
        try {
            br.close();
        } catch (IOException e) {
            logger.error("Failed closing Player InputStream");
        }
    }

    public void setPlayer(PlayerImpl player) {
        this.player = player;
    }




}
