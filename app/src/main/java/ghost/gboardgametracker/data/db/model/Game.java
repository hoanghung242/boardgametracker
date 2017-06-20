package ghost.gboardgametracker.data.db.model;

import ghost.gboardgametracker.task.exception.GameTitleNotSetException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by hoangnh on 3/4/17.
 */

@Entity
public class Game implements Validation{

    @Id(autoincrement = true)
    private Long id;

    @Property
    @NotNull
    private String gameTitle;

    @Property
    @NotNull
    private Long creationTime;

    @Property
    private Long lastUpdateTime;

    @Property
    private Long totalPlayTime;

    @Property
    private Boolean totalPlayTimeEnabled;

    @Property
    private Long playerTurnTime;

    @Property
    private Boolean playerTurnTimeEnabled;

    @Property
    private Boolean gameFinish;

    @Property
    private Long gameFinishInfoId;

    @Property
    private Long startingScore; //initialize scores for each player

    @Property
    private Boolean startingScoreEnabled;

    @Property
    private String scoreName; // different game has different type of score (money, point,...)

    @Property
    private Integer winningScoreConditionType; // 0: highest score win, 1: lowest score win

    @Property
    private Boolean RollingDiceEnabled;

    @Property
    private Integer numberOfRollingDices;

    @Generated(hash = 380959371)
    public Game() {
    }

    @Generated(hash = 1845602253)
    public Game(Long id, @NotNull String gameTitle, @NotNull Long creationTime,
            Long lastUpdateTime, Long totalPlayTime, Boolean totalPlayTimeEnabled,
            Long playerTurnTime, Boolean playerTurnTimeEnabled, Boolean gameFinish,
            Long gameFinishInfoId, Long startingScore, Boolean startingScoreEnabled,
            String scoreName, Integer winningScoreConditionType, Boolean RollingDiceEnabled,
            Integer numberOfRollingDices) {
        this.id = id;
        this.gameTitle = gameTitle;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.totalPlayTime = totalPlayTime;
        this.totalPlayTimeEnabled = totalPlayTimeEnabled;
        this.playerTurnTime = playerTurnTime;
        this.playerTurnTimeEnabled = playerTurnTimeEnabled;
        this.gameFinish = gameFinish;
        this.gameFinishInfoId = gameFinishInfoId;
        this.startingScore = startingScore;
        this.startingScoreEnabled = startingScoreEnabled;
        this.scoreName = scoreName;
        this.winningScoreConditionType = winningScoreConditionType;
        this.RollingDiceEnabled = RollingDiceEnabled;
        this.numberOfRollingDices = numberOfRollingDices;
    }

    @Override
    public void validate() {
        if (gameTitle == null || gameTitle.isEmpty()) {
            throw new GameTitleNotSetException();
        }
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameTitle() {
        return this.gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public Long getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Long getTotalPlayTime() {
        return this.totalPlayTime;
    }

    public void setTotalPlayTime(Long totalPlayTime) {
        this.totalPlayTime = totalPlayTime;
    }

    public Boolean getTotalPlayTimeEnabled() {
        return this.totalPlayTimeEnabled;
    }

    public void setTotalPlayTimeEnabled(Boolean totalPlayTimeEnabled) {
        this.totalPlayTimeEnabled = totalPlayTimeEnabled;
    }

    public Long getPlayerTurnTime() {
        return this.playerTurnTime;
    }

    public void setPlayerTurnTime(Long playerTurnTime) {
        this.playerTurnTime = playerTurnTime;
    }

    public Boolean getPlayerTurnTimeEnabled() {
        return this.playerTurnTimeEnabled;
    }

    public void setPlayerTurnTimeEnabled(Boolean playerTurnTimeEnabled) {
        this.playerTurnTimeEnabled = playerTurnTimeEnabled;
    }

    public Boolean getGameFinish() {
        return this.gameFinish;
    }

    public void setGameFinish(Boolean gameFinish) {
        this.gameFinish = gameFinish;
    }

    public Long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Long getStartingScore() {
        return this.startingScore;
    }

    public void setStartingScore(Long startingScore) {
        this.startingScore = startingScore;
    }

    public Boolean getStartingScoreEnabled() {
        return this.startingScoreEnabled;
    }

    public void setStartingScoreEnabled(Boolean startingScoreEnabled) {
        this.startingScoreEnabled = startingScoreEnabled;
    }

    public String getScoreName() {
        return this.scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public Integer getWinningScoreConditionType() {
        return this.winningScoreConditionType;
    }

    public void setWinningScoreConditionType(Integer winningScoreConditionType) {
        this.winningScoreConditionType = winningScoreConditionType;
    }

    public Long getGameFinishInfoId() {
        return this.gameFinishInfoId;
    }

    public void setGameFinishInfoId(Long gameFinishInfoId) {
        this.gameFinishInfoId = gameFinishInfoId;
    }

    public Boolean getRollingDiceEnabled() {
        return this.RollingDiceEnabled;
    }

    public void setRollingDiceEnabled(Boolean RollingDiceEnabled) {
        this.RollingDiceEnabled = RollingDiceEnabled;
    }

    public Integer getNumberOfRollingDices() {
        return this.numberOfRollingDices;
    }

    public void setNumberOfRollingDices(Integer numberOfRollingDices) {
        this.numberOfRollingDices = numberOfRollingDices;
    }
}
