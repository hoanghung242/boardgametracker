package ghost.gboardgametracker.ui.gamelog.model;

/**
 * Created by hoangnh on 3/9/17.
 */

public class GameLogUIModel {
    public Long id;
    public Long creationTime;
    public int actionType;
    public Long actionScore;
    public String playerName;
    public String otherPlayerName;

    public GameLogUIModel(Long id, Long creationTime, int actionType, Long actionScore, String playerName, String otherPlayerName) {
        this.id = id;
        this.creationTime = creationTime;
        this.actionType = actionType;
        this.actionScore = actionScore;
        this.playerName = playerName;
        this.otherPlayerName = otherPlayerName;
    }
}
