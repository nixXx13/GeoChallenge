package Player;

import Common.Converter;
import Common.GameData;
import Common.GameData.GameDataType;
import Common.GameStage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void send(GameDataType type,List<GameStage> gameStages) {

        Map<String,String> data = new HashMap<>();
        int i = 1;
        for(GameStage gs : gameStages){

            data.put( "q"+i , gs.getQuestion());
            data.put( "a"+i,  gs.getAnswer());
        }
        GameData gameData = new GameData(type,data);
        String json = Converter.toJson(gameData);
        sendPlayer(json);
        logger.debug(String.format("sent questions as json '%s",json));
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

