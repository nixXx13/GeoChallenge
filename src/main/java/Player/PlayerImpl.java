package Player;

import Common.GameData.GameDataType;
import GameManager.IGameManager;
import org.apache.log4j.Logger;
import Common.GameStage;
import Common.GameData;
import Common.INetworkConnector;

import java.util.List;
import java.util.Map;

public class PlayerImpl implements IPlayer,Runnable {

    private final static Logger logger = Logger.getLogger(PlayerImpl.class);

    private float score;
    private int id;
    private String playerName;
    private int questionIndex;
    private IGameManager gameManager;
    private List<GameStage> gameStages;

    private INetworkConnector networkConnector;

    PlayerImpl(int id, String playerName, INetworkConnector networkConnector){
        this.id = id;
        this.playerName = playerName;
        this.networkConnector = networkConnector;
        this.score = 0;
        questionIndex = 0;
    }

    public int getId() {
        return id;
    }

    public float getScore() {
        return score;
    }

    @Override
    public String getName() {
        return playerName;
    }

    public void init(IGameManager gameManager, List<GameStage> gameStages) {
        this.gameManager = gameManager;
        this.gameStages = gameStages;
    }

    public PlayerStatus getStatus(){
        if (gameStages.size() == questionIndex){
            return PlayerStatus.FINISHED;
        }
        return PlayerStatus.ACTIVE;
    }

    // acknoledge player that he is connected to server
    public void ack(String info){
        networkConnector.send(new GameData(GameDataType.ACK,info));
    }

    public void end( String endMsg ) {
        networkConnector.send(new GameData(GameDataType.END,endMsg));
    }

    public void grade(float newGrade) {
        this.score += newGrade;
        questionIndex+=1;
        networkConnector.send(new GameData(GameDataType.GRADE, String.valueOf(newGrade)));
    }

    public void update( String update) {
        networkConnector.send(new GameData(GameDataType.UPDATE, update));
    }

    // handle response from player
    public void handleResponse(GameData gameData) {
        switch (gameData.getType()) {
            case DATA:
                if (! (questionIndex < gameStages.size())) {
                    // TODO - UT this
                    logger.debug(String.format("Player %s answered all his questions already!",playerName));
                    break;
                }
                handleAnswer(gameData);
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
        networkConnector.send(gameStages);
        listen();
        networkConnector.terminate();
    }

    private void handleAnswer(GameData gameData){
        logger.debug(String.format("Handling answer #%d received from player %s",questionIndex,playerName));
        Map<String, String> data = gameData.getContent();
        // TODO - change strings to constants
        float time = Float.valueOf(data.get("time"));
        String answer = data.get("answer");
        gameManager.receiveAnswer(id, gameStages.get(questionIndex), answer, time);
    }

    private void listen(){
        logger.debug(String.format("Listening to player '%s' input",playerName));

        GameData gameData = networkConnector.read();
        while ( gameData != null && !GameData.GameDataType.END.equals(gameData.getType())) {
            handleResponse(gameData);
            gameData = networkConnector.read();
        }
        if (gameData == null) {
            logger.error(String.format("Error listening to player '%s' input",playerName));
            disconnect();
        }
        logger.debug(String.format("Stopped listening to player '%s' input",playerName));
    }
}
