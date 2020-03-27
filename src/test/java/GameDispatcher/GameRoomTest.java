package GameDispatcher;

import Common.GameType;
import GameExceptions.GameException;
import GameExceptions.GameNameException;
import GameExceptions.GameRoomException;
import Player.IPlayer;
import Player.PlayerFactory;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameRoomTest {
    private final static Logger logger = Logger.getLogger(GameRoomTest.class);

    @Test
    void roomFullTest() throws GameException {
        GameConfigImpl gameRoomConfig = new GameConfigImpl("player1","roomName",true,
                1,5, GameType.GameTypeEnum.TEST);
        GameRoom room = new GameRoom(gameRoomConfig);
        room.addPlayer(getPlayer());
        assertTrue(room.isFull());
    }

    @Test
    void playerExistsTest() throws GameException {
        GameConfigImpl gameRoomConfig = new GameConfigImpl("player1","roomName",true,
                1,5, GameType.GameTypeEnum.TEST);
        GameRoom room = new GameRoom(gameRoomConfig);
        try{
            room.addPlayer(getPlayer());
            room.addPlayer(getPlayer());
        }catch (GameRoomException e){
            logger.debug("Room is full");
        }
    }


    @Test
    void addSamePlayerToSameRoomTest() throws GameException {
        GameConfigImpl gameRoomConfig = new GameConfigImpl("player1","roomName",true,
                2,5, GameType.GameTypeEnum.TEST);
        GameRoom room = new GameRoom(gameRoomConfig);
        try{
            room.addPlayer(getPlayer());
            room.addPlayer(getPlayer());
        }catch (GameNameException e){
            logger.debug("Player with same name already exists in room");
        }
    }

    private IPlayer getPlayer(){
        GameConfigImpl gameConfig = new GameConfigImpl("player1","roomName",true,
                1,5, GameType.GameTypeEnum.TEST);
        return PlayerFactory.getPlayer(null,gameConfig);
    }
}