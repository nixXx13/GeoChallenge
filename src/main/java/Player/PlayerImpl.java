package Player;

import Common.GameData;
import Common.GameData.GameDataType;
import GameManager.IGameManager;
import Common.GameStage;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class PlayerImpl implements IPlayer,Runnable {

    // TODO - add UT
    final static Logger logger = Logger.getLogger(PlayerImpl.class);

    private float score;
    private int id;
    private String playerName;
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

    // acknoledge player that he is connected to server
    public void ack(String info){
        playerOut.send(GameDataType.ACK,info);
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

    public void handleResponse(GameData gameData) {
        switch (gameData.getType()) {
            case DATA:
                Map<String, String> data = gameData.getContent();
                float time = Float.valueOf(data.get("time"));
                String answer = data.get("answer");
                gameManager.receiveAnswer(id, answer, time);
                break;
            default:
                logger.warn(String.format("Unknown response received from player '%d' - '%s'", id, gameData.toString()));
                break;
        }
    }

    public void disconnect(){
        gameManager.receiveDisconnect(id);
    }

    public void run() {
        playerOut.send(gameStages);
        playerIn.setPlayer(this);
        playerIn.listen();

        // game ended - closing
        playerOut.close();
        playerIn.close();
        // TODO - close socket?
    }

}
