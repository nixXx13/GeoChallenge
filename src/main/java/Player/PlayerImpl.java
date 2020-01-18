package Player;

import Common.GameData.GameDataType;
import GameManager.IGameManager;
import Common.GameStage;

import java.util.List;

public class PlayerImpl implements IPlayer,Runnable {

    private float score;
    private int id;
    private IGameManager gameManager;
    private List<GameStage> gameStages;

    private PlayerIn playerIn;
    private PlayerOut playerOut;

    public PlayerImpl(int id, PlayerIn playerIn, PlayerOut playerOut){
        this.id = id;
        this.playerIn = playerIn;
        this.playerOut = playerOut;
    }


    public int getId() {
        return id;
    }

    public float getScore() {
        return score;
    }

    public void init(IGameManager gameManager, List<GameStage> gameStages) {
        this.gameManager = gameManager;
        this.gameStages = gameStages;
    }


    public void end( String msg) {
        playerOut.send(GameDataType.END, msg);
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
        playerOut.send(GameDataType.START,gameStages);
        playerIn.setPlayer(this);
        playerIn.listen();
    }

}
