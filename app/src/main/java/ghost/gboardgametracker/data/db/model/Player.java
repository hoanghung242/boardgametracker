package ghost.gboardgametracker.data.db.model;

import ghost.gboardgametracker.task.exception.PlayerNameNotSetException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by hoangnh on 3/4/17.
 */

@Entity
public class Player implements Validation{

    @Id(autoincrement = true)
    private Long id;

    @Property
    @NotNull
    private String name;

    @Generated(hash = 1354616888)
    public Player(Long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 30709322)
    public Player() {
    }

    @Override
    public void validate() {
        if (name == null || name.isEmpty()) {
            throw new PlayerNameNotSetException();
        }
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
