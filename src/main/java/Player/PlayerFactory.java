package Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PlayerFactory {

    public IPlayer getPlayer(Socket socket, int id) throws IOException {

        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream is = new ObjectInputStream(socket.getInputStream());

        PlayerIn playerIn = new PlayerIn(is);
        PlayerOut playerOut = new PlayerOut(os);

        return new PlayerImpl(id, playerIn, playerOut);

    }


}
