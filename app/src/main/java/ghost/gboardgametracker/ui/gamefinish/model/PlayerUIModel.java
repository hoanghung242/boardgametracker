package ghost.gboardgametracker.ui.gamefinish.model;

/**
 * Created by hoangnh on 3/5/17.
 */

public class PlayerUIModel{
    public Long id;
    public String name;
    public Long score = 0L;
    public int rank = 1;

    public static PlayerUIModel create(PlayerUIModel playerUIModel) {
        PlayerUIModel player = new PlayerUIModel();
        player.id = playerUIModel.id;
        player.score = playerUIModel.score;
        player.name = playerUIModel.name;

        return player;
    }
}
