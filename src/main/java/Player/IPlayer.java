package Player;

import GameManager.IGameManager;
import Models.GameStage;

import java.util.List;

public interface IPlayer extends Runnable {

    public enum UpdateType{
        STATUS,
        GRADE,
        END

    }
    public int getId();
    public float getScore();
    public void addScore(float newGrade);

    public void init(IGameManager gameManager, List<GameStage> gameStages);
    public void update(UpdateType type,String update);
    public void end(UpdateType type,String update);

    public void handleAnswer(String playerAnswer);
}

