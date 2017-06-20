package ghost.gboardgametracker.utils.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ghost.gboardgametracker.R;

public class GameDialogBuilder {
    private Context mContext;

    private View mContentView = null;

    private String mDialogTitle = "";
    private @DrawableRes int mDialogIconId = R.drawable.ic_info_white;

    private boolean mDialogDescriptionEnabled;
    private String mDialogDescription = "";

    private boolean mPositiveBtnEnabled;
    private String mPositiveBtnTitle = "";
    private OnClickListener mOnClickPositiveBtnListener;

    private boolean mNegativeBtnEnabled;
    private String mNegativeBtnTitle = "";
    private OnClickListener mOnClickNegativeBtnListener;

    private AlertDialog mAlertDialog;

    public GameDialogBuilder(Context context) {
        mContext = context;
    }

    public GameDialogBuilder setContentView(View contentView) {
        mContentView = contentView;
        return this;
    }

    public GameDialogBuilder setDialogTitle(@StringRes int dialogTitleId) {
        return setDialogTitle(mContext.getResources().getString(dialogTitleId));
    }

    public GameDialogBuilder setDialogTitle(String dialogTitle) {
        if (dialogTitle != null) {
            mDialogTitle = dialogTitle;
        }
        return this;
    }

    public GameDialogBuilder setDialogIcon(@DrawableRes int dialogIconId) {
        mDialogIconId = dialogIconId;
        return this;
    }

    public GameDialogBuilder setDialogDescription(@StringRes int dialogDescriptionId) {
        return setDialogDescription(mContext.getResources().getString(dialogDescriptionId));
    }

    public GameDialogBuilder setDialogDescription(String dialogDescription) {
        if (dialogDescription != null) {
            mDialogDescription = dialogDescription;
        }
        mDialogDescriptionEnabled = true;
        return this;
    }

    public GameDialogBuilder setPositiveButton(@StringRes int positiveBtnTitleId, OnClickListener onClickListener) {
        return setPositiveButton(mContext.getResources().getString(positiveBtnTitleId), onClickListener);
    }

    public GameDialogBuilder setPositiveButton(String positiveBtnTitle, OnClickListener onClickListener) {
        if (mPositiveBtnTitle != null) {
            mPositiveBtnTitle = positiveBtnTitle;
        }
        mOnClickPositiveBtnListener = onClickListener;
        mPositiveBtnEnabled = true;
        return this;
    }

    public GameDialogBuilder setNegativeButton(@StringRes int negativeBtnTitleId, OnClickListener onClickListener) {
        return setNegativeButton(mContext.getResources().getString(negativeBtnTitleId), onClickListener);
    }

    public GameDialogBuilder setNegativeButton(String negativeBtnTitle, OnClickListener onClickListener) {
        if (mNegativeBtnTitle != null) {
            mNegativeBtnTitle = negativeBtnTitle;
        }
        mOnClickNegativeBtnListener = onClickListener;
        mNegativeBtnEnabled = true;
        return this;
    }

    public AlertDialog create() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.default_game_dialog, null);
        builder.setView(dialogView);

        LinearLayout contentLayout = (LinearLayout) dialogView.findViewById(R.id.content_layout);
        TextView titleView = (TextView) dialogView.findViewById(R.id.dialog_title);
        ImageView iconView = (ImageView) dialogView.findViewById(R.id.dialog_icon);
        TextView descriptionView = (TextView) dialogView.findViewById(R.id.dialog_description);
        TextView positiveBtn = (TextView) dialogView.findViewById(R.id.positive_btn);
        TextView negativeBtn = (TextView) dialogView.findViewById(R.id.negative_btn);
        View buttonDevider = dialogView.findViewById(R.id.button_divider);

        if (mContentView != null) {
            contentLayout.addView(mContentView);
        }

        titleView.setText(mDialogTitle);
        iconView.setImageResource(mDialogIconId);

        if (mDialogDescriptionEnabled) {
            descriptionView.setVisibility(View.VISIBLE);
            descriptionView.setText(mDialogDescription);
        } else {
            descriptionView.setVisibility(View.GONE);
        }

        if (mPositiveBtnEnabled) {
            positiveBtn.setText(mPositiveBtnTitle);
            positiveBtn.setOnClickListener(v -> mOnClickPositiveBtnListener.onClick(mAlertDialog));
        }

        if (mNegativeBtnEnabled) {
            negativeBtn.setText(mNegativeBtnTitle);
            negativeBtn.setOnClickListener(v -> mOnClickNegativeBtnListener.onClick(mAlertDialog));
        } else {
            negativeBtn.setVisibility(View.GONE);
            buttonDevider.setVisibility(View.GONE);
        }

        mAlertDialog = builder.create();
        return mAlertDialog;
    }

    public interface OnClickListener {
        void onClick(DialogInterface dialog);
    }
}