package ghost.gboardgametracker;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import ghost.gboardgametracker.di.component.ApplicationComponent;
import ghost.gboardgametracker.di.component.DaggerApplicationComponent;
import ghost.gboardgametracker.di.module.ApplicationModule;
import io.fabric.sdk.android.Fabric;

/**
 * Created by hoangnh on 2/10/17.
 */

public class BoardGameTrackerApp extends Application {

    private ApplicationComponent mApplicationComponent;

    public static BoardGameTrackerApp getApp(Context context) {
        return (BoardGameTrackerApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        mApplicationComponent.inject(this);

    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    // replace test component for testing
    public void setApplicationComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}
