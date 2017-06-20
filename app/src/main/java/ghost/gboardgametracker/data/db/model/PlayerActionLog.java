package ghost.gboardgametracker.data.db.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by hoangnh on 3/6/17.
 */

@Entity
public class PlayerActionLog implements Validation{

    @Id(autoincrement = true)
    private Long id;

    @Property
    private Long creationTime;

    @Property
    private Integer actionType; //0: add, 1:remove, 2:transfer

    @Property
    private Long gameId;

    @Property
    private Long playerId;

    @Property
    private Long toPlayerId;

    @Property
    private Long scoreOffset;

    @Generated(hash = 955951743)
    public PlayerActionLog(Long id, Long creationTime, Integer actionType,
            Long gameId, Long playerId, Long toPlayerId, Long scoreOffset) {
        this.id = id;
        this.creationTime = creationTime;
        this.actionType = actionType;
        this.gameId = gameId;
        this.playerId = playerId;
        this.toPlayerId = toPlayerId;
        this.scoreOffset = scoreOffset;
    }

    @Generated(hash = 751504366)
    public PlayerActionLog() {
    }

    @Override
    public void validate() {

    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Integer getActionType() {
        return this.actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public Long getGameId() {
        return this.gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getScoreOffset() {
        return this.scoreOffset;
    }

    public void setScoreOffset(Long scoreOffset) {
        this.scoreOffset = scoreOffset;
    }

    public Long getToPlayerId() {
        return this.toPlayerId;
    }

    public void setToPlayerId(Long toPlayerId) {
        this.toPlayerId = toPlayerId;
    }
}
