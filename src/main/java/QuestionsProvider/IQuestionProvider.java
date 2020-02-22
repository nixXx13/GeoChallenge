package QuestionsProvider;

import Common.GameStage;

import java.util.List;

public interface IQuestionProvider {

    List<GameStage> getQuestions(int num);
}
