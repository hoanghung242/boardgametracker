package ghost.gboardgametracker.ui.main;

import ghost.gboardgametracker.data.DataManager;
import ghost.gboardgametracker.data.db.model.Game;
import ghost.gboardgametracker.data.db.model.Player;
import ghost.gboardgametracker.ui.base.BasePresenter;
import ghost.gboardgametracker.ui.main.model.GameUIModel;
import ghost.gboardgametracker.utils.AppConstants;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoangnh on 2/10/17.
 */

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V> implements MainMvpPresenter<V>{

    private List<GameUIModel> mGameViewList;

    @Inject
    public MainPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void fetchGameViewList() {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().loadAllGames(0, AppConstants.GAME_LIST.DISPLAY_NUMBER_OF_GAME_PER_PAGE)
                .subscribeOn(Schedulers.io())
                .flatMapIterable(gameList -> gameList)
                .flatMap(game -> getGameUIModel(game))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gameViewList -> {
                    mGameViewList = gameViewList;
                    getMvpView().hideLoading();
                    getMvpView().displayGameList(gameViewList);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    @Override
    public void onClickGame(Long gameId) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().loadGame(gameId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(game -> {
                    getMvpView().hideLoading();
                    if (game.getGameFinish() != null && game.getGameFinish()) {
                        getMvpView().navigateToGameFinish(game.getId(), game.getGameTitle());
                    } else {
                        getMvpView().navigateToGamePlay(game.getId());
                    }
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    @Override
    public void onClickFilterAll() {
        getMvpView().displayGameList(mGameViewList);
    }

    @Override
    public void onClickFilterPlaying() {
        if (mGameViewList != null) {
            List<GameUIModel> playingList = new ArrayList<>();
            for (GameUIModel gameUIModel : mGameViewList) {
                if (gameUIModel.game.getGameFinish() == null || !gameUIModel.game.getGameFinish()) {
                    playingList.add(gameUIModel);
                }
            }
            getMvpView().displayGameList(playingList);
        }
    }

    @Override
    public void onClickFilterFinished() {
        if (mGameViewList != null) {
            List<GameUIModel> finishedList = new ArrayList<>();
            for (GameUIModel gameUIModel : mGameViewList) {
                if (gameUIModel.game.getGameFinish() != null && gameUIModel.game.getGameFinish()) {
                    finishedList.add(gameUIModel);
                }
            }
            getMvpView().displayGameList(finishedList);
        }
    }

    @Override
    public void onLoadMoreGames(int totalItemsCount) {
        getCompositeDisposable().add(getDataManager().loadAllGames(totalItemsCount, AppConstants.GAME_LIST.DISPLAY_NUMBER_OF_GAME_PER_PAGE)
                .subscribeOn(Schedulers.io())
                .flatMapIterable(gameList -> gameList)
                .flatMap(game -> getGameUIModel(game))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gameViewList -> {
                    getMvpView().hideLoading();
                    if (gameViewList != null) {
                        mGameViewList.addAll(gameViewList);
                    }
                    getMvpView().displayMoreGames(gameViewList);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    private Observable<GameUIModel> getGameUIModel(Game game) {

        Observable<Game> loadGame = getDataManager().loadGame(game.getId());
        Observable<List<Player>> loadPlayerList = getDataManager().loadPlayerListFromGame(game.getId());

        return Observable.zip(loadGame, loadPlayerList, (game1, players) -> {
            GameUIModel gameUIModel = new GameUIModel();
            gameUIModel.game = game1;
            gameUIModel.numberOfPlayer = players.size();
            return gameUIModel;
        });
    }
}
