package ghost.gboardgametracker.ui.base;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import ghost.gboardgametracker.R;
import ghost.gboardgametracker.ui.navigation.NavigationItemEnum;
import ghost.gboardgametracker.utils.AppConstants;

/**
 * Created by hoangnh on 2/13/17.
 */

public abstract class BaseDrawerActivity extends BaseActivity {

    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    protected NavigationItemEnum mSelfNavigationItem;
    private Handler mHandler;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mHandler = new Handler();

        setupNavigation();
        setupActionBar();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            // Close drawer first if it is open
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void setupNavigation() {
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_view);
        mSelfNavigationItem = getSelfNavigationItem();
        mNavigationView.setCheckedItem(mSelfNavigationItem.getId());

        mNavigationView.setNavigationItemSelectedListener(item -> {
            mDrawer.closeDrawer(GravityCompat.START);

            final NavigationItemEnum navigationItem = NavigationItemEnum.getById(item.getItemId());
            if (navigationItem != mSelfNavigationItem) {
                // put a short delay for drawer close animation
                mHandler.postDelayed(() -> {
                    if (navigationItem.getClassToLaunch() != null) {
                        Intent intent = new Intent(BaseDrawerActivity.this, navigationItem.getClassToLaunch());
                        BaseDrawerActivity.this.finish();
                        BaseDrawerActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        BaseDrawerActivity.this.startActivity(intent);
                    }
                }, AppConstants.NAVIGATION_DRAWER_LAUNCH_DELAY);
            }

            return true;
        });
    }

    protected void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    protected abstract NavigationItemEnum getSelfNavigationItem();

    private void setSelectedNavDrawerItem(NavigationItemEnum item) {
        if (mNavigationView != null && item != NavigationItemEnum.INVALID) {
            mNavigationView.getMenu().findItem(item.getId()).setChecked(true);
        }
    }
}
