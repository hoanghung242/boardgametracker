package ghost.gboardgametracker.ui.newgame;

import ghost.gboardgametracker.data.DataManager;
import ghost.gboardgametracker.data.db.model.Game;
import ghost.gboardgametracker.data.db.model.Player;
import ghost.gboardgametracker.task.exception.MinimumNumberOfPlayerException;
import ghost.gboardgametracker.ui.base.BasePresenter;
import ghost.gboardgametracker.ui.newgame.model.NewGameUIModel;
import ghost.gboardgametracker.utils.AppConstants;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoangnh on 2/13/17.
 */

public class NewGamePresenter<V extends NewGameMvpView> extends BasePresenter<V> implements NewGameMvpPresenter<V> {

    private NewGameUIModel mNewGameUIModel;

    @Inject
    public NewGamePresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void onViewLoaded() {
        super.onViewLoaded();
    }

    @Override
    public void onClickStartGame(NewGameUIModel newGameUIModel) {

        getMvpView().showLoading();
        getMvpView().hideKeyboard();

        List<Player> playerList = new ArrayList<>(); // only select non-empty emplayer
        for (Player player : newGameUIModel.playerList) {
            if (player != null && player.getName() != null) {
                player.setName(player.getName().trim().replaceAll("\\s+", " "));
                if (!player.getName().isEmpty()) {
                    playerList.add(player);
                }
            }
        }

        Game game = newGameUIModel.game;
        game.setId(null);

        getCompositeDisposable().add(getDataManager().saveGame(game, playerList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gameId -> {
                    getMvpView().hideLoading();
                    getMvpView().navigationToGamePlay(gameId);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    @Override
    public void loadGameInfo(Long gameId) {

        if (gameId == AppConstants.NULL_ID) {
            mNewGameUIModel = new NewGameUIModel();
            mNewGameUIModel.game = new Game();
            mNewGameUIModel.playerList = new ArrayList<>();

            for (int i = 0; i < AppConstants.GAME_PLAY.MINIMUM_NUMBER_OF_PLAYERS; ++i) {
                mNewGameUIModel.playerList.add(new Player());
            }

            getMvpView().displayGameInfo(mNewGameUIModel);
            return;
        }

        getMvpView().showLoading();
        getCompositeDisposable().add(getNewGameUIModel(gameId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newGameUIModel -> {
                            getMvpView().hideLoading();
                            mNewGameUIModel = newGameUIModel;
                            getMvpView().displayGameInfo(mNewGameUIModel);
                        },
                        throwable -> {
                            getMvpView().hideLoading();
                            getMvpView().onReceiveThrowable(throwable);}
                ));
    }

    @Override
    public void addNewPlayer() {
        if (mNewGameUIModel.playerList.size() < AppConstants.GAME_PLAY.MAXIMUM_NUMBER_OF_PLAYERS) {
            mNewGameUIModel.playerList.add(new Player());
            getMvpView().addNewPlayer(mNewGameUIModel, mNewGameUIModel.playerList.size() - 1);
        }
    }

    @Override
    public void removePlayer(int playerPos) {
        if (playerPos < mNewGameUIModel.playerList.size()) {
            if (mNewGameUIModel.playerList.size() > AppConstants.GAME_PLAY.MINIMUM_NUMBER_OF_PLAYERS) {
                mNewGameUIModel.playerList.remove(playerPos);
                getMvpView().removePlayer(mNewGameUIModel, playerPos);
            } else {
                getMvpView().onReceiveThrowable(new MinimumNumberOfPlayerException());
            }
        }
    }

    private Observable<NewGameUIModel> getNewGameUIModel(final Long gameId) {

        Observable<Game> loadGame = getDataManager().loadGame(gameId);
        Observable<List<Player>> loadPlayerList = getDataManager().loadPlayerListFromGame(gameId);

        return Observable.zip(loadGame, loadPlayerList, (game, players) -> {
            NewGameUIModel newGameUIModel = new NewGameUIModel();
            newGameUIModel.game = game;
            newGameUIModel.playerList = players;
            return newGameUIModel;
        });
    }
}
