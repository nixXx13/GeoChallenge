package Common;

import java.util.List;

public class GameStage {

    private String question;
    private List<String> possibileAnswers;
    private String answer;

    public GameStage(String question, List<String> possibileAnswers, String answer){
        this.question = question;
        this.possibileAnswers = possibileAnswers;
        this.answer=answer;
    }

    public boolean isAnswerCorrect(String someAnswer){
        return answer.equals(someAnswer);
    }

    public String getQuestion(){
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}

