package GameDispatcher;

import GameExceptions.GameException;
import GameExceptions.GameNameException;
import GameExceptions.GameRoomException;
import Common.GameType.GameTypeEnum;
import Player.IPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static GameDispatcher.GameDispatcherUtils.isStringInList;

public class GameRoom {

    private String name;
    private int size;
    private GameTypeEnum type;
    private int questionsNumber;
    private List<IPlayer> players;

    public GameRoom(IRoomConfig roomConfig){
        name = roomConfig.getRoomName();
        size = roomConfig.getRoomSize();
        type = roomConfig.getRoomType();
        questionsNumber = roomConfig.getQuestionsNumber();
        players = new ArrayList<>();
    }

    public boolean isFull(){
        return players.size() == size;
    }

    public void addPlayer(IPlayer player) throws GameException {
        if (isFull()){
            throw new GameRoomException("Room is full");
        }
        String playerName = player.getName();
        if (doesNameExists(playerName)){
            // name already exists in room
            throw new GameNameException(String.format("Player name '%s' is already taken",playerName));
        }
        players.add(player);
    }

    public String getName() {
        return name;
    }

    public List<IPlayer> getPlayers() {
        return players;
    }

    private boolean doesNameExists( String newName){
        List<String> playerNames = players.stream().map(IPlayer::getName).collect(Collectors.toList());
        return isStringInList(playerNames, newName);
    }

    public GameTypeEnum getType() {
        return type;
    }

    public int getQuestionsNumber() {
        return questionsNumber;
    }
}
