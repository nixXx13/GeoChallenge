package GameDispatcher;

import GameConfig.IRoomConfig;
import Player.IPlayer;

public interface IGameDispacher {

    void dispatch(IPlayer player, IRoomConfig roomConfig);
}
