package ghost.gboardgametracker.ui.base;

import android.support.annotation.StringRes;

/**
 * Created by hoangnh on 2/10/17.
 */

public interface MvpView {

    void showLoading();

    void hideLoading();

    void onError(@StringRes int resId);

    void onError(String errorMessage);

    void onReceiveThrowable(Throwable throwable);

    void hideKeyboard();

    void showKeyboard();
}
