package GameManager;

import Common.GameStage;
import Common.GameType.GameTypeEnum;
import Player.IPlayer;
import Player.PlayerImpl;
import Util.Constants;
import Util.RestUtil;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameManagerImpl implements IGameManager {
    private final static Logger logger = Logger.getLogger(GameManagerImpl.class);

    private Map<String,IPlayer> players;
    private List<GameStage> gameStages;
    private GameTypeEnum gameType;

    private int activePlayers;
    private int id;

    private final String MSG_UPDATE = "Player '%s' scored %.2f.";
    private final String MSG_END    = "Player '%s' finished game.";

    public GameManagerImpl(Map<String,IPlayer> players, List<GameStage> gameStages, GameTypeEnum gameType){
        id = new Random().nextInt(1000);
        logger.info(String.format("GM%d:initializing game manager with %d players and %d game stages",id,players.size(),gameStages.size()));
        this.players = players;
        this.gameStages = gameStages;
        activePlayers=players.size();
        this.gameType = gameType;
    }

    public void startGame(){
        for(String playerName: players.keySet()){
            IPlayer player = players.get(playerName);

            logger.info(String.format("GM%d:player '%s' - game starting",id,player.getName()));
            player.init(this, gameStages);
            Thread p = new Thread(player);
            p.start();
        }
    }

    public void receiveAnswer(String playerName, GameStage gameStage, String answer, float time){
        IPlayer currPlayer = players.get(playerName);

        // grading answer and sending score
        float answerScore = gradeGameStage(gameStage, answer, time);
        logger.info(String.format("GM%d:'%s' scored '%f'. '%s', player answer:'%s', answer:'%s'",id,
                currPlayer.getName(), answerScore,gameStage.getQuestion(),answer,gameStage.getAnswer()));
        currPlayer.grade(answerScore);

        // sending updates
        String updateMsg = String.format(MSG_UPDATE, playerName, answerScore);
        updateAllPlayers(updateMsg);

        if (currPlayer.getStatus() == PlayerImpl.PlayerStatus.FINISHED) {
            String endMsg = String.format(MSG_END, playerName);
            logger.debug(String.format("GM%d:'%s' - finished. total score %f. Waiting for other players" +
                    " to finish.", id,currPlayer.getName(),currPlayer.getScore()));
            currPlayer.update(endMsg);
            decreaseActivePlayersCounter(playerName);
        }
    }

    public void receiveDisconnect(String playerName){
        decreaseActivePlayersCounter(playerName);
    }

    private float gradeGameStage(GameStage gameStage, String answer, float time) {
        float grade = gameStage.isAnswerCorrect(answer) ? 1 : 0 ;
        grade*=time;
        logger.trace(String.format("Graded '%f' answer '%s' for question %s",grade,answer,gameStage.getQuestion()));
        return grade;
    }

    private synchronized void decreaseActivePlayersCounter(String playerName){
        // TODO - prevent edge case of same player decreasing counter twice

        // decreasing active players counter
        activePlayers-=1;
        logger.debug(String.format("GM%d:'%s' - decreased active players number to %d",id,playerName,activePlayers));

        // checking if current finishing player is the last player
        if(activePlayers==0){
            logger.info(String.format("GM%d:'%s' - last active player. Notifying rest of the players game ended",id,playerName));
            String summary = getSummary();

            String bestScores = getBestScores(summary, gameType);

            for (String cPlayerName : players.keySet()) {
                IPlayer player = players.get(cPlayerName);
                logger.trace(String.format("GM%d:'%s' - updating with end msg '%s'",id, player.getName(), summary));
                // TODO - fix shortcut
                player.end(summary + "&" + bestScores);
            }
        }
    }

    private String getSummary(){
        StringBuilder sb = new StringBuilder();
        players.forEach((k,v)-> sb.append(v.getName()).append(" - ").append(String.format("%.2f",v.getScore())).append(";"));
        return sb.toString();
    }

    private String getBestScores(String gameScores, GameTypeEnum gameType){
        // TODO - implement game type in lambda
        String gameTypeStr = "geo";
        if (gameType.equals(GameTypeEnum.MATH)){
            gameTypeStr = "math";
        }

        // preparing scores dict for best scores service
        HashMap<String,Float> scoresMap = new HashMap<>();
        players.forEach((k,v)-> scoresMap.put(k,v.getScore()));
        Gson gson = new Gson();
        String jsonScores = gson.toJson(scoresMap);

        String bestScoresJson = "";
        try {
            bestScoresJson = RestUtil.post(Constants.SCORES_SERVICE_URL,jsonScores);
        } catch (IOException e) {
            logger.debug(String.format("Failed fetching best scores for game type %s",gameType.toString()));
            e.printStackTrace();
        }
        return bestScoresJson;
    }

    private void updateAllPlayers(String updateMsg){
        for (String playerName : players.keySet()) {
            IPlayer player = players.get(playerName);
            logger.trace(String.format("GM%d:updating player '%s' with update msg '%s'",id ,player.getName(), updateMsg));
            player.update(updateMsg);
        }
    }

}
