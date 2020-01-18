package Player;

import Models.GameStage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static Utils.ConnectionUtils.sendObjectOutputStream;

public class PlayerOut {

    private ObjectOutputStream os;

    public PlayerOut(ObjectOutputStream os){
        this.os = os;
    }

    public void send(String update){
        try {
            sendObjectOutputStream(os,update);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO - handle send fail!
    }

    public void send(List<GameStage> gameStages) {

        // TODO - temp
        StringBuilder Questions =  new StringBuilder();
        for(GameStage gs : gameStages){
            Questions.append(gs.getQuestion());
            Questions.append(":");
        }
        send(Questions.toString());
    }
}

