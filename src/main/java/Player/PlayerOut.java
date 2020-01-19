package Player;

import Common.Converter;
import Common.GameData;
import Common.GameData.GameDataType;
import Common.GameStage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import static Common.ConnectionUtils.sendObjectOutputStream;

public class PlayerOut {

    final static Logger logger = Logger.getLogger(PlayerOut.class);
    private ObjectOutputStream os;

    public PlayerOut(ObjectOutputStream os){
        this.os = os;
    }

    public void send(GameDataType type, String msg){
        GameData gameData = new GameData(type,msg);
        String json = Converter.toJson(gameData);

        sendPlayer(json);
    }

    public void send(List<GameStage> gameStages) {

        String json = Converter.toJson(gameStages);
        sendPlayer(json);
        logger.debug(String.format("sent questions as json '%s",json));
    }

    public void close(){
        try {
            os.close();
        } catch (IOException e) {
            logger.error("Failed closing Player OutputStream");
        }
    }

    private void sendPlayer(String json){
        try {
            sendObjectOutputStream(os,json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO - handle send fail!
    }
}

