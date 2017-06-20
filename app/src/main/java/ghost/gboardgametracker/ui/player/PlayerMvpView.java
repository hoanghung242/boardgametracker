package ghost.gboardgametracker.ui.player;

import ghost.gboardgametracker.data.db.model.Player;
import ghost.gboardgametracker.ui.base.MvpView;

import java.util.List;

/**
 * Created by hoangnh on 2/14/17.
 */

public interface PlayerMvpView extends MvpView {

    void refreshPlayerList(List<Player> playerList);

}
