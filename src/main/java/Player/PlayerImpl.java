package Player;

import GameManager.IGameManager;
import Models.GameStage;

import java.util.List;
import java.util.logging.Logger;

public class PlayerImpl implements IPlayer,Runnable {

    private final static Logger LOGGER = Logger.getLogger(PlayerImpl.class.getName());

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

    public void init(IGameManager gameManager, List<GameStage> gameStages) {
        this.gameManager = gameManager;
        this.gameStages = gameStages;
    }

    public void end(UpdateType type, String update) {
        playerOut.send(update);
    }

    public void handleAnswer(String playerAnswer) {
        float time = 1;
        gameManager.receiveAnswer(id,playerAnswer,time);
    }

    public int getId() {
        return id;
    }

    public float getScore() {
        return score;
    }

    public void addScore(float newGrade) {
        this.score += newGrade;
        // TODO - notify player
    }

    public void update(UpdateType type, String update) {
        playerOut.send(update);
    }

    public void run() {
        playerOut.send(gameStages);
        playerIn.setPlayer(this);
        playerIn.listen();
    }

}
