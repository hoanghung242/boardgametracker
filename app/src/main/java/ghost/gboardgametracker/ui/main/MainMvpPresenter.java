package ghost.gboardgametracker.ui.main;

import ghost.gboardgametracker.di.scope.PerActivity;
import ghost.gboardgametracker.ui.base.MvpPresenter;

/**
 * Created by hoangnh on 2/10/17.
 */

@PerActivity
public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void fetchGameViewList();

    void onClickGame(Long gameId);

    void onClickFilterAll();

    void onClickFilterPlaying();

    void onClickFilterFinished();

    void onLoadMoreGames(int totalItemsCount);
}
