package ghost.gboardgametracker.ui.gamedetail;

import ghost.gboardgametracker.data.DataManager;
import ghost.gboardgametracker.data.db.model.Game;
import ghost.gboardgametracker.data.db.model.Player;
import ghost.gboardgametracker.ui.base.BasePresenter;
import ghost.gboardgametracker.ui.gamedetail.model.GameDetailUIModel;
import ghost.gboardgametracker.utils.AppConstants;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by hoangnh on 2/21/17.
 */

public class GameDetailPresenter<V extends GameDetailMvpView> extends BasePresenter<V> implements
        GameDetailMvpPresenter<V> {

    @Inject
    public GameDetailPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void onViewLoaded() {
        super.onViewLoaded();
    }

    @Override
    public void loadGameInfo(Long gameId) {

        getMvpView().showLoading();
        getCompositeDisposable().add(getGameDetailUIModel(gameId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gameDetailUIModel -> {
                    getMvpView().hideLoading();
                    getMvpView().displayGameInfo(gameDetailUIModel);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    private Observable<GameDetailUIModel> getGameDetailUIModel(final Long gameId) {

        Observable<Game> loadGame = getDataManager().loadGame(gameId);
        Observable<List<Player>> loadPlayerList = getDataManager().loadPlayerListFromGame(gameId);

        return Observable.zip(loadGame, loadPlayerList, (game, players) -> {
            GameDetailUIModel gameDetailUIModel = new GameDetailUIModel();
            gameDetailUIModel.title = game.getGameTitle();
            gameDetailUIModel.gameId = gameId;
            return gameDetailUIModel;
        });
    }
}
