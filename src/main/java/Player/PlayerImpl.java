package Player;

import Common.GameData.GameDataType;
import GameManager.IGameManager;
import Common.GameStage;

import java.util.List;

public class PlayerImpl implements IPlayer,Runnable {

    // TODO - add minor UT

    private float score;
    private int id;
    private int questionsAnswered;
    private IGameManager gameManager;
    private List<GameStage> gameStages;

    private PlayerIn playerIn;
    private PlayerOut playerOut;

    public PlayerImpl(int id, PlayerIn playerIn, PlayerOut playerOut){
        this.id = id;
        this.playerIn = playerIn;
        this.playerOut = playerOut;
        this.score = 0;
        questionsAnswered = 0;
    }


    public int getId() {
        return id;
    }

    public float getScore() {
        return score;
    }

    public int getQuestionsAnswered() {
        return questionsAnswered;
    }

    @Override
    public void setQuestionsAnswered(int questionsAnswered) {
        this.questionsAnswered = questionsAnswered;
    }

    public void init(IGameManager gameManager, List<GameStage> gameStages) {
        this.gameManager = gameManager;
        this.gameStages = gameStages;
    }


    public void end( String endMsg ) {
        playerOut.send(GameDataType.END,endMsg);
    }

    public void grade(float newGrade) {
        this.score += newGrade;
        playerOut.send(GameDataType.GRADE, String.valueOf(newGrade));
    }

    public void update( String update) {
        playerOut.send(GameDataType.UPDATE, update);
    }

    public void handleAnswer(String playerAnswer) {
        float time = 1;
        gameManager.receiveAnswer(id,playerAnswer,time);
    }

    public void run() {
        playerOut.send(gameStages);
        playerIn.setPlayer(this);
        playerIn.listen();

        // game ended - closing
        playerOut.close();
        playerIn.close();
    }

}
