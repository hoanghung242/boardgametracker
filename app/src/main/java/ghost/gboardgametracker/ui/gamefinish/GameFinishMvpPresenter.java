package ghost.gboardgametracker.ui.gamefinish;

import ghost.gboardgametracker.di.scope.PerActivity;
import ghost.gboardgametracker.ui.base.MvpPresenter;

/**
 * Created by hoangnh on 3/27/17.
 */

@PerActivity
public interface GameFinishMvpPresenter<V extends GameFinishMvpView> extends MvpPresenter<V> {

    void onCreated(Long gameId);

    void onClickPlayBtn();

    void onClickGameLogMenu();
}
