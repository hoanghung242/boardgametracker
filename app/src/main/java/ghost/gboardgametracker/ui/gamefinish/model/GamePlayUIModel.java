package ghost.gboardgametracker.ui.gamefinish.model;

import ghost.gboardgametracker.data.db.model.Game;
import ghost.gboardgametracker.data.db.model.GameFinishInfo;

import java.util.List;

/**
 * Created by hoangnh on 3/5/17.
 */

public class GamePlayUIModel {
    public Game game;
    public GameFinishInfo gameFinishInfo;
    public List<PlayerUIModel> gamePlayerUIModelList;
}
