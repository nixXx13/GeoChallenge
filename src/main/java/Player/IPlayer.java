package Player;

import Common.GameData;
import GameManager.IGameManager;
import Common.GameStage;

import java.util.List;

public interface IPlayer extends Runnable {

    enum PlayerStatus{
        ACTIVE,
        FINISHED
    }

    float getScore();
    String getName();
    PlayerStatus getStatus();

    void init(IGameManager gameManager, List<GameStage> gameStages);

    void ack(String info);
    void update(String msg);
    void grade(float newGrade);
    void end(String msg);
    void disconnect();
    void error(String errorMsg);

    void handleResponse(GameData gameData);
}

