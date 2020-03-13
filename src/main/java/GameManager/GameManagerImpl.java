package GameManager;

import Common.GameStage;
import Player.IPlayer;
import Player.PlayerImpl;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameManagerImpl implements IGameManager {
    private final static Logger logger = Logger.getLogger(GameManagerImpl.class);

    private Map<Integer,IPlayer> players;
    private List<GameStage> gameStages;

    private int activePlayers;
    private int id;

    private final String MSG_UPDATE = "Player %s scored %.2f.";
    private final String MSG_END    = "Player %s finished game.";


    public GameManagerImpl(Map<Integer,IPlayer> players, List<GameStage> gameStages){
        id = new Random().nextInt(1000);
        logger.info(String.format("GM%d:initializing game manager with %d players and %d game stages",id,players.size(),gameStages.size()));
        this.players = players;
        this.gameStages = gameStages;
        activePlayers=players.size();
    }

    public void startGame(){
        for(Integer playerId : players.keySet()){
            IPlayer player = players.get(playerId);

            logger.info(String.format("GM%d:player %d - starting game for player",id,playerId));
            player.init(this, gameStages);
            Thread p = new Thread(player);
            p.start();
        }
    }

    public void receiveAnswer(int playerId, GameStage gameStage, String answer, float time){
        IPlayer currPlayer = players.get(playerId);

        // grading answer and sending score
        float answerScore = gradeGameStage(gameStage, answer, time);
        logger.info(String.format("GM%d:player %d - scored '%f' when answering '%s' in question '%s'",id, currPlayer.getId(), answerScore,answer,gameStage.getQuestion()));
        currPlayer.grade(answerScore);

        // sending updates
        String updateMsg = String.format(MSG_UPDATE, playerId, answerScore);
        updateAllPlayers(updateMsg);

        if (currPlayer.getStatus() == PlayerImpl.PlayerStatus.FINISHED) {
            String endMsg = String.format(MSG_END, playerId);
            logger.debug(String.format("GM%d:player %d - finished answering questions with score %f. Waiting for other players" +
                    " to finish.", id,currPlayer.getId(),currPlayer.getScore()));
            currPlayer.update(endMsg);
            decreaseActivePlayersCounter(playerId);
        }
    }

    public void receiveDisconnect(int playerId){
        decreaseActivePlayersCounter(playerId);
    }

    private float gradeGameStage(GameStage gameStage, String answer, float time) {
        float grade = gameStage.isAnswerCorrect(answer) ? 1 : 0 ;
        grade*=time;
        logger.trace(String.format("Graded '%f' answer '%s' for question %s",grade,answer,gameStage.getQuestion()));
        return grade;
    }

    private synchronized void decreaseActivePlayersCounter(int playerId){
        // TODO - prevent edge case of same player decreasing counter twice

        // decreasing active players counter
        activePlayers-=1;
        logger.debug(String.format("GM%d:player %d - decreased active players number to %d",id,playerId,activePlayers));

        // checking if current finishing player is the last player
        if(activePlayers==0){
            logger.info(String.format("GM%d:player %d - last active player. Notifying rest of the players game ended",id,playerId));
            String summary = getSummary();
            for (Integer cPlayerId : players.keySet()) {
                IPlayer player = players.get(cPlayerId);
                logger.trace(String.format("GM%d:player %d - updating with end msg '%s'",id, player.getId(), summary));
                player.end(summary);
            }
        }
    }

    private String getSummary(){
        StringBuilder sb = new StringBuilder();
        players.forEach((k,v)-> sb.append(v.getName()).append(" - ").append(String.format("%.2f",v.getScore())).append(";"));
        return sb.toString();
    }

    private void updateAllPlayers(String updateMsg){
        for (Integer playerId : players.keySet()) {
            IPlayer player = players.get(playerId);
            logger.trace(String.format("GM%d:updating player %d with update msg '%s'",id ,player.getId(), updateMsg));
            player.update(updateMsg);
        }
    }

}
