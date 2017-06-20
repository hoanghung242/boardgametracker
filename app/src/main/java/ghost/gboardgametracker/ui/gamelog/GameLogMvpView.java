package ghost.gboardgametracker.ui.gamelog;

import ghost.gboardgametracker.ui.base.MvpView;
import ghost.gboardgametracker.ui.gamelog.model.GameLogUIModel;

import java.util.List;

/**
 * Created by hoangnh on 3/9/17.
 */

public interface GameLogMvpView extends MvpView{
    void displayPlayerActionLogs(List<GameLogUIModel> gameLogUIModelList);
}
