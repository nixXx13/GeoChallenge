package GameConfig;

import Common.GameType.GameTypeEnum;

public class GameConfigImpl implements IRoomConfig,IPlayerConfig{

    private String playerName;
    private boolean isCreate;
    private String roomName;
    private GameTypeEnum roomType;
    private int roomSize;
    private int questionsNumber;

    public GameConfigImpl(String playerName, String roomName, boolean isCreate, int size, int questionsNumber, GameTypeEnum type ){
        this.playerName = playerName;
        this.roomName = roomName;
        this.isCreate = isCreate;
        this.questionsNumber = questionsNumber;
        this.roomType = type;
        this.roomSize = size;
    }

    public int getRoomSize() {
        return roomSize;
    }

    public int getQuestionsNumber() {
        return questionsNumber;
    }

    public GameTypeEnum getRoomType() {
        return roomType;
    }

    public String getRoomName() {
        return roomName;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public String getPlayerName() {
        return playerName;
    }
}
