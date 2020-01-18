package Common;

import com.google.gson.Gson;

import java.util.Map;

public class Converter {

    private static Gson gson = new Gson();

    public static String toJson(GameData gameData){
        return gson.toJson(gameData);
    }

    public static String toJson(Map data){
        return gson.toJson(data);
    }
}
