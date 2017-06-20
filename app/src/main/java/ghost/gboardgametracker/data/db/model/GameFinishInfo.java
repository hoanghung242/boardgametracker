package ghost.gboardgametracker.data.db.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by hoangnh on 3/27/17.
 */

@Entity
public class GameFinishInfo {

    @Id(autoincrement = true)
    private Long id;

    @Property
    private Long gameId;

    @Property
    private Long creationDate;

    @Property
    private Long winnerPlayerId;

    @Generated(hash = 419975450)
    public GameFinishInfo(Long id, Long gameId, Long creationDate,
            Long winnerPlayerId) {
        this.id = id;
        this.gameId = gameId;
        this.creationDate = creationDate;
        this.winnerPlayerId = winnerPlayerId;
    }

    @Generated(hash = 1917775170)
    public GameFinishInfo() {
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

    public Long getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getWinnerPlayerId() {
        return this.winnerPlayerId;
    }

    public void setWinnerPlayerId(Long winnerPlayerId) {
        this.winnerPlayerId = winnerPlayerId;
    }
}
