package ghost.gboardgametracker.di.component;

import dagger.Component;
import ghost.gboardgametracker.ui.gamedetail.GameDetailActivity;
import ghost.gboardgametracker.ui.gamefinish.GameFinishActivity;
import ghost.gboardgametracker.ui.gamelog.GameLogActivity;
import ghost.gboardgametracker.ui.gamesetting.GameSettingActivity;
import ghost.gboardgametracker.ui.main.MainActivity;
import ghost.gboardgametracker.di.module.ActivityModule;
import ghost.gboardgametracker.di.scope.PerActivity;
import ghost.gboardgametracker.ui.newgame.NewGameActivity;
import ghost.gboardgametracker.ui.play.GamePlayActivity;
import ghost.gboardgametracker.ui.player.PlayerActivity;
import ghost.gboardgametracker.ui.playtime.PlayTimeActivity;

/**
 * Created by hoangnh on 2/10/17.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);

    void inject(NewGameActivity activity);

    void inject(PlayerActivity activity);

    void inject(GameDetailActivity activity);

    void inject(GamePlayActivity activity);

    void inject(GameSettingActivity activity);

    void inject(GameLogActivity activity);

    void inject(GameFinishActivity activity);
}
