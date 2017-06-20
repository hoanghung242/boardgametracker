package ghost.gboardgametracker.di.module;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import ghost.gboardgametracker.data.AppDataManager;
import ghost.gboardgametracker.data.DataManager;
import ghost.gboardgametracker.data.db.AppDbHelper;
import ghost.gboardgametracker.data.db.DbHelper;
import ghost.gboardgametracker.data.prefs.AppPreferencesHelper;
import ghost.gboardgametracker.data.prefs.PreferencesHelper;
import ghost.gboardgametracker.di.scope.ApplicationContext;
import ghost.gboardgametracker.di.scope.DatabaseInfo;
import ghost.gboardgametracker.di.scope.PreferenceInfo;
import ghost.gboardgametracker.utils.AppConstants;

import javax.inject.Singleton;

/**
 * Created by hoangnh on 2/10/17.
 */

@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideApplicationContext() {
        return mApplication;
    }

    @Provides
    @DatabaseInfo
    String provideDatabaseName() {
        return AppConstants.DB_NAME;
    }

    @Provides
    @PreferenceInfo
    String providePreferenceName() {
        return AppConstants.PREF_NAME;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Provides
    @Singleton
    DbHelper provideDbHelper(AppDbHelper appDbHelper) {
        return appDbHelper;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }
}
