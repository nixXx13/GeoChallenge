package GameManager;

import Common.GameStage;
import GameDispatcher.GameRoom;
import Player.IPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManagerFactory {

    public static IGameManager getGameManager(GameRoom gameRoom, List<GameStage> gameStages){

        List<IPlayer> players = gameRoom.getPlayers();
        Map<Integer,IPlayer> playersMap = new HashMap<>();

        for( IPlayer player : players){
            playersMap.put(player.getId(),player);
        }

        return new GameManagerImpl(playersMap,gameStages);
    }
}
