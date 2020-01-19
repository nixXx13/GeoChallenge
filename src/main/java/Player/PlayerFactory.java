package Player;

import java.io.*;
import java.net.Socket;

public class PlayerFactory {

    public IPlayer getPlayer(Socket socket, int id) throws IOException {

        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        InputStreamReader is = new InputStreamReader(socket.getInputStream());

        PlayerIn playerIn = new PlayerIn(is);
        PlayerOut playerOut = new PlayerOut(os);

        return new PlayerImpl(id, playerIn, playerOut);

    }


}
