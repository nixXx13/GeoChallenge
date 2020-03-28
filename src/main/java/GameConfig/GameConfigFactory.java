package GameConfig;

import Common.GameType;
import GameExceptions.GameConfigException;

public class GameConfigFactory {

    private static final int MAX_ROOM_SIZE = 4;
    private static final int MIN_ROOM_SIZE = 1;

    public GameConfigImpl getGameConfig(String playerName, String roomName, boolean isCreate, int size,
                                        int questionsNumber, GameType.GameTypeEnum type ) throws GameConfigException {

        // input validation
        if(size<MIN_ROOM_SIZE || size>MAX_ROOM_SIZE){
            throw new GameConfigException(String.format("Valid room size is %d-%d",MIN_ROOM_SIZE,MAX_ROOM_SIZE));
        }
        return new GameConfigImpl(playerName, roomName, isCreate, size, questionsNumber, type);
    }

}
