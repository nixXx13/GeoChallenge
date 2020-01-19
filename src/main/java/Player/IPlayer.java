package Player;

import GameManager.IGameManager;
import Common.GameStage;

import java.util.List;

public interface IPlayer extends Runnable {

    public int getId();
    public float getScore();

    public void init(IGameManager gameManager, List<GameStage> gameStages);

    public void update(String msg);
    public void grade(float newGrade);
    public void end(String msg);

    public void handleAnswer(String playerAnswer);
}

