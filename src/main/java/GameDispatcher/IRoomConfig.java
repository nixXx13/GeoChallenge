package GameDispatcher;

import Common.GameType;

public interface IRoomConfig {

    int getRoomSize();

    int getQuestionsNumber();

    GameType.GameTypeEnum getRoomType();

    String getRoomName();

    boolean isCreate();

}
