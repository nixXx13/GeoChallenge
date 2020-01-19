package Player;

import java.io.*;
import java.net.Socket;

public class PlayerFactory {

    public IPlayer getPlayer(Socket socket, int id) throws IOException {

        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        PlayerIn playerIn = new PlayerIn(br);
        PlayerOut playerOut = new PlayerOut(os);

        return new PlayerImpl(id, playerIn, playerOut);

    }


}
