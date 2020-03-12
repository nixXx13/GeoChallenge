package Player;

import Common.NetworkConnectorImpl;
import java.net.Socket;

public class PlayerFactory {

    public IPlayer getPlayer(Socket socket, int id){

        NetworkConnectorImpl serverConnector = new NetworkConnectorImpl(socket);
        serverConnector.init();
        return new PlayerImpl(id, "player" + id, serverConnector);

    }


}
