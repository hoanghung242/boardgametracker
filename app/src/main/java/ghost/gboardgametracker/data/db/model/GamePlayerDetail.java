package ghost.gboardgametracker.data.db.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by hoangnh on 3/4/17.
 */

@Entity
public class GamePlayerDetail implements Validation{

    @Id(autoincrement = true)
    private Long id;

    @Property
    @NotNull
    private Long gameId;

    @Property
    @NotNull
    private Long playerId;

    @Property
    private Long score;

    @Generated(hash = 841733458)
    public GamePlayerDetail(Long id, @NotNull Long gameId, @NotNull Long playerId,
            Long score) {
        this.id = id;
        this.gameId = gameId;
        this.playerId = playerId;
        this.score = score;
    }

    @Generated(hash = 143055027)
    public GamePlayerDetail() {
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

    public Long getScore() {
        return this.score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}
