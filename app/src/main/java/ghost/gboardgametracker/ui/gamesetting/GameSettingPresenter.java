package ghost.gboardgametracker.ui.gamesetting;

import ghost.gboardgametracker.data.DataManager;
import ghost.gboardgametracker.data.db.model.Game;
import ghost.gboardgametracker.ui.base.BasePresenter;
import ghost.gboardgametracker.utils.AppConstants;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;

/**
 * Created by hoangnh on 3/6/17.
 */

public class GameSettingPresenter<V extends GameSettingMvpView> extends BasePresenter<V> implements
        GameSettingMvpPresenter<V> {

    private Long mGameId;
    private Game mGame;

    @Inject
    public GameSettingPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void loadGameSettingInfo(Long gameId) {
        mGameId = gameId;
        if (gameId == AppConstants.NULL_ID) {
            return;
        }

        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().loadGame(gameId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(game -> {
                    getMvpView().hideLoading();
                    mGame = game;
                    showSettings();
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    @Override
    public void setTotalPlayTime(Long totalPlayTime) {
        if (!totalPlayTime.equals(mGame.getTotalPlayTime()) || !mGame.getTotalPlayTimeEnabled()) {
            // Something change, must update data
            if (totalPlayTime <= 0) {
                // zero time, considered as not set
                mGame.setTotalPlayTime(0L);
                mGame.setTotalPlayTimeEnabled(false);
            } else {
                mGame.setTotalPlayTime(totalPlayTime);
                mGame.setTotalPlayTimeEnabled(true);
            }
            updateTotalPlayTime();
        }
    }

    @Override
    public void enableTotalPlayTime(Boolean isEnabled) {
        if (!isEnabled.equals(mGame.getTotalPlayTimeEnabled())) {
            if (isEnabled && (mGame.getTotalPlayTime() == null || mGame.getTotalPlayTime() <= 0)) {
                getMvpView().showTotalTimePickerDialog(0L);
            } else {
                mGame.setTotalPlayTimeEnabled(isEnabled);
                updateTotalPlayTime();
            }
        }
    }

    @Override
    public void onCancelSetTotalTime() {
        showTotalTime();
    }

    @Override
    public void setPlayerTurnTime(Long totalTime) {
        if (!totalTime.equals(mGame.getPlayerTurnTime()) || !mGame.getPlayerTurnTimeEnabled()) {
            if (totalTime <= 0) {
                mGame.setPlayerTurnTime(0L);
                mGame.setPlayerTurnTimeEnabled(false);
            } else {
                mGame.setPlayerTurnTime(totalTime);
                mGame.setPlayerTurnTimeEnabled(true);
            }
            updatePlayerTurnTime();
        }
    }

    @Override
    public void enablePlayerTurnTime(Boolean isEnabled) {
        if (!isEnabled.equals(mGame.getPlayerTurnTimeEnabled())) {
            if (isEnabled && (mGame.getPlayerTurnTime() == null || mGame.getPlayerTurnTime() <= 0)) {
                getMvpView().showPlayerTurnTimePickerDialog(0L);
            } else {
                mGame.setPlayerTurnTimeEnabled(isEnabled);
                updatePlayerTurnTime();
            }
        }
    }

    @Override
    public void onCancelSetPlayerTurnTime() {
        showPlayerTurnTime();
    }

    @Override
    public void enableStartingScore(Boolean isEnabled) {
        if (!isEnabled.equals(mGame.getStartingScoreEnabled())) {
            if (isEnabled && (mGame.getStartingScore() == null || mGame.getStartingScore() == 0)) {
                getMvpView().showStartingScorePickerDialog(0L);
            } else {
                mGame.setStartingScoreEnabled(isEnabled);
                updateStartingScore();
            }
        }
    }

    @Override
    public void setStartingScore(Long startingScore) {
        if (!startingScore.equals(mGame.getStartingScore()) || !mGame.getStartingScoreEnabled()) {
            if (startingScore == 0) {
                mGame.setStartingScore(0L);
                mGame.setStartingScoreEnabled(false);
            } else {
                mGame.setStartingScore(startingScore);
                mGame.setStartingScoreEnabled(true);
            }
            updateStartingScore();
        }
    }

    @Override
    public void onCancelSetStartingScore() {
        showStartingScore();
    }

    @Override
    public void toggleWinningScoreCondition() {
        if (mGame.getWinningScoreConditionType() == null) {
            mGame.setWinningScoreConditionType(0);
        }

        mGame.setWinningScoreConditionType(1 - mGame.getWinningScoreConditionType());
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().updateGame(mGame)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(update -> {
                    getMvpView().hideLoading();
                    showWinningScoreCondition();
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    @Override
    public void enableRollingDice(Boolean isEnabled) {
        if (!isEnabled.equals(mGame.getRollingDiceEnabled())) {
            if (isEnabled && (mGame.getNumberOfRollingDices() == null || mGame.getNumberOfRollingDices() <= 0)) {
                getMvpView().showRollingDicePickerDialog(0);
            } else {
                mGame.setRollingDiceEnabled(isEnabled);
                updateRollingDice();
            }
        }
    }

    @Override
    public void setNumberOfRollingDice(Integer numberOfRollingDice) {
        if (!numberOfRollingDice.equals(mGame.getNumberOfRollingDices()) || !mGame.getRollingDiceEnabled()) {
            if (numberOfRollingDice == 0) {
                mGame.setNumberOfRollingDices(0);
                mGame.setRollingDiceEnabled(false);
            } else {
                mGame.setNumberOfRollingDices(numberOfRollingDice);
                mGame.setRollingDiceEnabled(true);
            }
            updateRollingDice();
        }
    }

    private void updateRollingDice() {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().updateGame(mGame)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(update -> {
                    getMvpView().hideLoading();
                    showRollingDice();
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    private void updatePlayerTurnTime() {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().updateGame(mGame)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(update -> {
                    getMvpView().hideLoading();
                    showPlayerTurnTime();
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    private void updateTotalPlayTime() {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().updateGame(mGame)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(update -> {
                    getMvpView().hideLoading();
                    showTotalTime();
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    private void updateStartingScore() {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().updateGame(mGame)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(update -> {
                    getMvpView().hideLoading();
                    showStartingScore();
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    private void showSettings() {
        //showTotalTime();
        showWinningScoreCondition();
        showPlayerTurnTime();
        showStartingScore();
        showRollingDice();
    }

    private void showTotalTime() {
        getMvpView().displayTotalTimeSettings(mGame.getTotalPlayTimeEnabled(), mGame.getTotalPlayTime());
    }

    private void showPlayerTurnTime() {
        getMvpView().displayPlayerTurnTimeSettings(mGame.getPlayerTurnTimeEnabled(), mGame.getPlayerTurnTime());
    }

    private void showStartingScore() {
        getMvpView().showStartingScoreSetting(mGame.getStartingScoreEnabled(), mGame.getStartingScore());
    }

    private void showWinningScoreCondition() {
        getMvpView().showWinningScoreConditionSetting(mGame.getWinningScoreConditionType());
    }

    private void showRollingDice() {
        getMvpView().showRollingDiceSetting(mGame.getRollingDiceEnabled(), mGame.getNumberOfRollingDices());
    }
}
