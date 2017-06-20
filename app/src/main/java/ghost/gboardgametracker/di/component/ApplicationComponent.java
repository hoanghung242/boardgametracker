package ghost.gboardgametracker.di.component;

import android.app.Application;
import android.content.Context;
import dagger.Component;
import ghost.gboardgametracker.BoardGameTrackerApp;
import ghost.gboardgametracker.data.DataManager;
import ghost.gboardgametracker.di.module.ApplicationModule;
import ghost.gboardgametracker.di.scope.ApplicationContext;

import javax.inject.Singleton;

/**
 * Created by hoangnh on 2/10/17.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(BoardGameTrackerApp app);

    @ApplicationContext
    Context context();

    Application application();

    DataManager dataManager();
}
