package Player;

import Common.INetworkConnector;
import GameConfig.IPlayerConfig;

public class PlayerFactory {

    public static IPlayer getPlayer(INetworkConnector networkConnector, IPlayerConfig playerConfig){
        String name = playerConfig.getPlayerName();
        return new PlayerImpl(name, networkConnector);
    }

}
