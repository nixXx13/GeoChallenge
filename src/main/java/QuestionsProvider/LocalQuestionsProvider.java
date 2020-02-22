package QuestionsProvider;

import Common.GameStage;

import java.util.ArrayList;
import java.util.List;

public class LocalQuestionsProvider implements IQuestionProvider {

    @Override
    public List<GameStage> getQuestions(int num) {
            ArrayList<GameStage> qs = new ArrayList<>();

            List<String> pAnswers = new ArrayList<>();
            pAnswers.add("1");
            pAnswers.add("2");
            pAnswers.add("3");
            pAnswers.add("4");
            GameStage gameStage1 = new GameStage("1+1",pAnswers,"2");
            GameStage gameStage2 = new GameStage("1+2",pAnswers,"3");

            qs.add(gameStage1);
            qs.add(gameStage2);

            return qs;
    }

}
