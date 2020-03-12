package GameManager;

import Common.GameStage;

public interface IGameManager {

        void startGame();

        void receiveAnswer(int currPlayerId, GameStage gameStage, String answer, float time);

        void receiveDisconnect(int playerId);
}



