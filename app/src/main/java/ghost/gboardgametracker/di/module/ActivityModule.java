package ghost.gboardgametracker.di.module;

import android.app.Activity;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import ghost.gboardgametracker.di.scope.ActivityContext;
import ghost.gboardgametracker.di.scope.PerActivity;
import ghost.gboardgametracker.ui.gamedetail.GameDetailMvpPresenter;
import ghost.gboardgametracker.ui.gamedetail.GameDetailMvpView;
import ghost.gboardgametracker.ui.gamedetail.GameDetailPresenter;
import ghost.gboardgametracker.ui.gamefinish.GameFinishMvpPresenter;
import ghost.gboardgametracker.ui.gamefinish.GameFinishMvpView;
import ghost.gboardgametracker.ui.gamefinish.GameFinishPresenter;
import ghost.gboardgametracker.ui.gamelog.GameLogMvpPresenter;
import ghost.gboardgametracker.ui.gamelog.GameLogMvpView;
import ghost.gboardgametracker.ui.gamelog.GameLogPresenter;
import ghost.gboardgametracker.ui.gamesetting.GameSettingMvpPresenter;
import ghost.gboardgametracker.ui.gamesetting.GameSettingMvpView;
import ghost.gboardgametracker.ui.gamesetting.GameSettingPresenter;
import ghost.gboardgametracker.ui.main.MainMvpPresenter;
import ghost.gboardgametracker.ui.main.MainMvpView;
import ghost.gboardgametracker.ui.main.MainPresenter;
import ghost.gboardgametracker.ui.newgame.NewGameMvpPresenter;
import ghost.gboardgametracker.ui.newgame.NewGameMvpView;
import ghost.gboardgametracker.ui.newgame.NewGamePresenter;
import ghost.gboardgametracker.ui.play.GamePlayMvpPresenter;
import ghost.gboardgametracker.ui.play.GamePlayMvpView;
import ghost.gboardgametracker.ui.play.GamePlayPresenter;
import ghost.gboardgametracker.ui.player.PlayerMvpPresenter;
import ghost.gboardgametracker.ui.player.PlayerMvpView;
import ghost.gboardgametracker.ui.player.PlayerPresenter;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by hoangnh on 2/10/17.
 */

@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }

    @Provides
    @ActivityContext
    Context provideActivityContext() {
        return mActivity;
    }

    @Provides
    @PerActivity
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    @PerActivity
    MainMvpPresenter<MainMvpView> provideMainMvpPresenter(MainPresenter<MainMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    NewGameMvpPresenter<NewGameMvpView> provideNewGameMvpPresenter(NewGamePresenter<NewGameMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    PlayerMvpPresenter<PlayerMvpView> providePlayerMvpPresenter(PlayerPresenter<PlayerMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    GameDetailMvpPresenter<GameDetailMvpView> provideGameDetailMvpPresenter(GameDetailPresenter<GameDetailMvpView>
                                                                                    presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    GamePlayMvpPresenter<GamePlayMvpView> provideGamePlayMvpPresenter(GamePlayPresenter<GamePlayMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    GameSettingMvpPresenter<GameSettingMvpView> provideGameSettingMvpPresenter
            (GameSettingPresenter<GameSettingMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    GameLogMvpPresenter<GameLogMvpView> provideGameLogMvpPresenter(GameLogPresenter<GameLogMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    GameFinishMvpPresenter<GameFinishMvpView> provideGameFinishMvpPresenter(GameFinishPresenter<GameFinishMvpView> presenter) {
        return presenter;
    }
}
