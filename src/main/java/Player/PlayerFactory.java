package Player;

import Common.INetworkConnector;

public class PlayerFactory {

    public static IPlayer getPlayer(INetworkConnector networkConnector, int id){
        return new PlayerImpl(id, "player" + id, networkConnector);
    }


}
