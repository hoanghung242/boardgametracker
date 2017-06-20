package ghost.gboardgametracker.ui.main;

import ghost.gboardgametracker.ui.base.MvpView;
import ghost.gboardgametracker.ui.main.model.GameUIModel;

import java.util.List;

/**
 * Created by hoangnh on 2/10/17.
 */

public interface MainMvpView extends MvpView{

    void displayGameList(List<GameUIModel> gameViewList);

    void displayMoreGames(List<GameUIModel> gameViewList);

    void navigateToGamePlay(Long gameId);

    void navigateToGameFinish(Long gameId, String gameTitle);
}
