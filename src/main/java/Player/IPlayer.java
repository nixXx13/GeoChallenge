package Player;

import GameManager.IGameManager;
import Common.GameStage;

import java.util.List;

public interface IPlayer extends Runnable {

    int getId();
    float getScore();
    int getQuestionsAnswered();
    void setQuestionsAnswered(int questionsAnswered);

    void init(IGameManager gameManager, List<GameStage> gameStages);

    void ack(String info);
    void update(String msg);
    void grade(float newGrade);
    void end(String msg);
    void disconnect();

    void handleAnswer(String playerAnswer);
}

