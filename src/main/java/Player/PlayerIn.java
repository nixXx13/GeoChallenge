package Player;

import java.io.ObjectInputStream;

import static Common.ConnectionUtils.readObjectInputStream;

public class PlayerIn {

    private ObjectInputStream is;
    private IPlayer player;

    public PlayerIn(ObjectInputStream is){
        this.is = is;
    }

    public void listen() {
        String playerInput = readObjectInputStream(is);
        while ( playerInput != null){
            player.handleAnswer(playerInput);
            playerInput = readObjectInputStream(is);
        }
        // TODO - Player exited/finished/connection terminated

    }

    public void setPlayer(PlayerImpl player) {
        this.player = player;
    }




}
