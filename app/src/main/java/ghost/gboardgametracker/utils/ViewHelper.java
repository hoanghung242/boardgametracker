package ghost.gboardgametracker.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import ghost.gboardgametracker.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by hoangnh on 2/10/17.
 */

public final class ViewHelper {

    private static final java.text.SimpleDateFormat sSimpleDateFormat = new java.text.SimpleDateFormat("HH:mm " +
            "dd-MM-yyyy", Locale.ENGLISH);

    private static final DecimalFormat sDecimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static String getDateString(long milliseconds) {
        return sSimpleDateFormat.format(new Date(milliseconds));
    }

    public static String getTimeDescription(Context context, long totalInMiliSeconds) {
        long hour = TimeUnit.MILLISECONDS.toHours(totalInMiliSeconds);
        long minute = TimeUnit.MILLISECONDS.toMinutes(totalInMiliSeconds) - TimeUnit.HOURS.toMinutes(hour);
        long second = TimeUnit.MILLISECONDS.toSeconds(totalInMiliSeconds) - TimeUnit.HOURS.toSeconds(hour)
                - TimeUnit.MINUTES.toSeconds(minute);

        StringBuilder stringBuilder = new StringBuilder();
        String separator = "";
        if (hour > 0) {
            stringBuilder.append(separator);
            stringBuilder.append(context.getString(R.string.format_hours, hour));
            separator = ", ";
        }
        if (minute > 0) {
            stringBuilder.append(separator);
            stringBuilder.append(context.getString(R.string.format_minutes, minute));
            separator = ", ";
        }
        if (second > 0) {
            stringBuilder.append(separator);
            stringBuilder.append(context.getString(R.string.format_seconds, second));
        }
        return stringBuilder.toString();
    }

    public static String getTimeCountDownDescription(Context context, long totalInMiliSeconds) {
        long hour = TimeUnit.MILLISECONDS.toHours(totalInMiliSeconds);
        long minute = TimeUnit.MILLISECONDS.toMinutes(totalInMiliSeconds) - TimeUnit.HOURS.toMinutes(hour);
        long second = TimeUnit.MILLISECONDS.toSeconds(totalInMiliSeconds) - TimeUnit.HOURS.toSeconds(hour)
                - TimeUnit.MINUTES.toSeconds(minute);

        return context.getString(R.string.format_countdown_timer, hour, minute, second);
    }

    public static String getFormatScore(Long score) {
        sDecimalFormat.applyPattern("#,###");
        return sDecimalFormat.format(score);
    }

    public static String getNumberOfDiceFormat(Context context, Integer numberOfRollingDices) {
        if (numberOfRollingDices == null) {
            numberOfRollingDices = 1;
        }

        switch (numberOfRollingDices) {
            case 0:
                return context.getResources().getString(R.string.no_dice);
            case 1:
                return context.getResources().getString(R.string.one_dice);
            case 2:
                return context.getResources().getString(R.string.two_dices);
            case 3:
                return context.getResources().getString(R.string.three_dices);
            case 4:
                return context.getResources().getString(R.string.four_dices);
            case 5:
                return context.getResources().getString(R.string.five_dices);
            case 6:
                return context.getResources().getString(R.string.six_dices);
            case 7:
                return context.getResources().getString(R.string.seven_dices);
            case 8:
                return context.getResources().getString(R.string.eight_dices);
            default:
                return context.getResources().getString(R.string.nine_dices);
        }
    }
}
