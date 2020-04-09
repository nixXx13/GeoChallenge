package GameManager;

import Common.GameStage;
import Common.GameType.GameTypeEnum;
import GameDispatcher.GameRoom;
import Player.IPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManagerFactory {

    public static IGameManager getGameManager(GameRoom gameRoom, List<GameStage> gameStages, GameTypeEnum gameType){

        List<IPlayer> players = gameRoom.getPlayers();
        Map<String,IPlayer> playersMap = new HashMap<>();

        for( IPlayer player : players){
            playersMap.put(player.getName(),player);
        }

        return new GameManagerImpl(playersMap,gameStages,gameType);
    }
}
