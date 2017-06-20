package ghost.gboardgametracker.ui.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import ghost.gboardgametracker.BoardGameTrackerApp;
import ghost.gboardgametracker.R;
import ghost.gboardgametracker.di.component.ActivityComponent;
import ghost.gboardgametracker.di.component.DaggerActivityComponent;
import ghost.gboardgametracker.di.module.ActivityModule;
import ghost.gboardgametracker.task.exception.GameTitleNotSetException;
import ghost.gboardgametracker.task.exception.MinimumNumberOfPlayerException;
import ghost.gboardgametracker.task.exception.NullIdsRelationException;
import ghost.gboardgametracker.task.exception.PlayerNameNotSetException;
import ghost.gboardgametracker.ui.main.MainActivity;
import ghost.gboardgametracker.utils.ViewHelper;

/**
 * Created by hoangnh on 2/10/17.
 */

public abstract class BaseActivity extends AppCompatActivity implements MvpView, BaseFragment.Callback{

    private ProgressDialog mProgressDialog;
    private ActivityComponent mActivityComponent;
    private long mLastBackPressTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(BoardGameTrackerApp.getApp(this).getApplicationComponent())
                .build();

        componentInjection();
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            if (this instanceof MainActivity) {
                //press back button within two seconds, exit the app
                if (System.currentTimeMillis() - mLastBackPressTime > 2000) {
                    showSnackBarMessage(R.string.cp_press_again_to_exit);
                    mLastBackPressTime = System.currentTimeMillis();
                } else {
                    super.onBackPressed();
                }
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                this.finish();
                this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                this.startActivity(intent);
            }
        } else {
            super.onBackPressed();
        }
    }

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    @Override
    public void showLoading() {
        hideLoading();
        mProgressDialog = ViewHelper.showLoadingDialog(this);
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        onError(getString(resId));
    }

    @Override
    public void onError(String errorMessage) {
        if (errorMessage != null) {
            showSnackBarMessage(errorMessage);
        } else {
            showSnackBarMessage(getString(R.string.unknown_error));
        }
    }

    @Override
    public void onReceiveThrowable(Throwable throwable) {
        if (throwable instanceof GameTitleNotSetException) {
            onError(R.string.exception_game_title_not_set);

        } else if (throwable instanceof MinimumNumberOfPlayerException) {
            onError(R.string.exception_game_must_have_at_least_two_players);

        } else if (throwable instanceof NullIdsRelationException) {
            onError(R.string.exception_null_ids_for_relation);

        } else if (throwable instanceof PlayerNameNotSetException) {
            onError(R.string.exception_player_name_not_set);

        } else {
            onError(throwable.getMessage());
        }
    }

    @Override
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void showKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        }
    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }


    protected abstract void componentInjection();

    protected void showSnackBarMessage(@StringRes int resId) {
        showSnackBarMessage(getString(resId));
    }

    protected void showSnackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }
}
