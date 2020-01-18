package Common;

import java.util.HashMap;
import java.util.Map;

public class GameData {

    private Map<String,String> content;
    private GameDataType type;

    public enum GameDataType{
        START,
        DATA,
        UPDATE,
        GRADE,
        END

    }

    public GameData(GameDataType type, String content){
        this.type = type;
        this.content = new HashMap<>();
        this.content.put("msg",content);
    }

    public GameData(GameDataType type, Map<String,String> content){
        this.type = type;
        this.content = content;
    }

    public GameDataType getType() {
        return type;
    }

    public Map<String,String> getContent() {
        return content;
    }
}
