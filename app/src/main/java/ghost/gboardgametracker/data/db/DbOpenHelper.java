package ghost.gboardgametracker.data.db;

import android.content.Context;
import com.orhanobut.logger.Logger;
import ghost.gboardgametracker.data.db.model.DaoMaster;
import ghost.gboardgametracker.data.db.model.GameDao;
import ghost.gboardgametracker.data.db.model.GamePlayerDetailDao;
import ghost.gboardgametracker.data.db.model.PlayerActionLogDao;
import ghost.gboardgametracker.data.db.model.PlayerDao;
import ghost.gboardgametracker.di.scope.ApplicationContext;
import ghost.gboardgametracker.di.scope.DatabaseInfo;
import org.greenrobot.greendao.database.Database;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by hoangnh on 2/10/17.
 */

@Singleton
public class DbOpenHelper extends DaoMaster.OpenHelper {

    @Inject
    public DbOpenHelper(@ApplicationContext Context context, @DatabaseInfo String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Logger.d("Upgrading schema from version " + oldVersion + " to " + newVersion + " by migrating all" + " tables data");

        MigrationHelper.getInstance().migrate(db,
                GameDao.class,
                GamePlayerDetailDao.class,
                PlayerDao.class,
                PlayerActionLogDao.class);
    }
}