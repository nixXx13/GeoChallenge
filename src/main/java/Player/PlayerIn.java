package Player;

import Common.GameData;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;

import static Common.ConnectionUtils.read;

public class PlayerIn {

    final static Logger logger = Logger.getLogger(PlayerIn.class);

    private ObjectInputStream is;
    private IPlayer player;


    public PlayerIn(ObjectInputStream is){
        this.is = is;
    }

    public void listen() {
        logger.debug(String.format("Listening to player '%d' input",player.getId()));

        GameData gameData = read(is);
        while ( gameData != null && !GameData.GameDataType.END.equals(gameData.getType())) {
            player.handleResponse(gameData);
            gameData = read(is);
        }
        if (gameData == null) {
            logger.error(String.format("Error listening to player '%d' input",player.getId()));
            player.disconnect();
        }
        logger.debug(String.format("Stopped listening to player '%d' input",player.getId()));
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
