package ghost.gboardgametracker.ui.gamesetting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.flexbox.FlexboxLayout;
import com.orhanobut.logger.Logger;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import ghost.gboardgametracker.R;
import ghost.gboardgametracker.ui.base.BaseActivity;
import ghost.gboardgametracker.utils.AppConstants;
import ghost.gboardgametracker.utils.ViewHelper;
import ghost.gboardgametracker.utils.widget.dialog.GameDialogBuilder;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class GameSettingActivity extends BaseActivity implements GameSettingMvpView{

    private static final String EXTRA_KEY_GAME_ID = "game_id";

    @BindView(R.id.settings_layout)
    LinearLayout settingLayout;

    @Inject
    GameSettingMvpPresenter<GameSettingMvpView> mPresenter;

    private SettingItemViewHolder mTotalTimeSettingViewHolder;
    private SettingItemViewHolder mPlayerTurnTimeSettingViewHolder;
    private SettingItemViewHolder mStartingScoreSettingViewHolder;
    private SettingItemViewHolder mWinningScoreConditionSettingViewHolder;
    private SettingItemViewHolder mRollingDiceSettingViewHolder;

    private TimePickerDialog.OnTimeSetListener mOnTotalTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
            Long totalTime = TimeUnit.HOURS.toMillis(hourOfDay)
                    + TimeUnit.MINUTES.toMillis(minute)
                    + TimeUnit.SECONDS.toMillis(second);
            mPresenter.setTotalPlayTime(totalTime);
        }
    };
    private DialogInterface.OnCancelListener mOnCancelTotalTimeSetListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            mPresenter.onCancelSetTotalTime();
        }
    };

    public static void start(Context context, Long gameId) {
        Intent starter = new Intent(context, GameSettingActivity.class);
        starter.putExtra(EXTRA_KEY_GAME_ID, gameId);
        context.startActivity(starter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setting);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Long gameId = getIntent().getLongExtra(EXTRA_KEY_GAME_ID, AppConstants.NULL_ID);
        mPresenter.loadGameSettingInfo(gameId);
    }

    @Override
    protected void componentInjection() {
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
    }

    @Override
    public void displayTotalTimeSettings(Boolean enable, final Long totalTimeInMilliSeconds) {
        if (mTotalTimeSettingViewHolder == null) {
            mTotalTimeSettingViewHolder = new SettingItemViewHolder();
            settingLayout.addView(mTotalTimeSettingViewHolder.contentView);
        }
        mTotalTimeSettingViewHolder.title.setText(R.string.total_time);

        if (totalTimeInMilliSeconds == null || totalTimeInMilliSeconds == 0) {
            // Show hint message if the time hasn't been set
            mTotalTimeSettingViewHolder.description.setText(R.string.select_total_time_for_this_game);
        } else {
            mTotalTimeSettingViewHolder.description.setText(ViewHelper.getTimeDescription(this, totalTimeInMilliSeconds));
        }

        mTotalTimeSettingViewHolder.active.setChecked(enable != null && enable);
        mTotalTimeSettingViewHolder.active.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPresenter.enableTotalPlayTime(isChecked);
        });

        mTotalTimeSettingViewHolder.contentView.setOnClickListener(v -> showTotalTimePickerDialog(totalTimeInMilliSeconds));
    }

    @Override
    public void displayPlayerTurnTimeSettings(Boolean enable, Long totalTimeInMilliSeconds) {
        if (mPlayerTurnTimeSettingViewHolder == null) {
            mPlayerTurnTimeSettingViewHolder = new SettingItemViewHolder();
            settingLayout.addView(mPlayerTurnTimeSettingViewHolder.contentView);
        }
        mPlayerTurnTimeSettingViewHolder.title.setText(R.string.player_turn_time);

        if (totalTimeInMilliSeconds == null || totalTimeInMilliSeconds == 0) {
            // Show hint message if the time hasn't been set
            mPlayerTurnTimeSettingViewHolder.description.setText(R.string.set_time_for_each_turn);
        } else {
            mPlayerTurnTimeSettingViewHolder.description.setText(ViewHelper.getTimeDescription(this, totalTimeInMilliSeconds));
        }

        mPlayerTurnTimeSettingViewHolder.active.setChecked(enable != null && enable);
        mPlayerTurnTimeSettingViewHolder.active.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPresenter.enablePlayerTurnTime(isChecked);
        });

        mPlayerTurnTimeSettingViewHolder.contentView.setOnClickListener(v -> showPlayerTurnTimePickerDialog(totalTimeInMilliSeconds));
    }

    @Override
    public void showTotalTimePickerDialog(Long totalTime) {
        showTimePickerDialog(totalTime, mOnTotalTimeSetListener, mOnCancelTotalTimeSetListener);
    }

    @Override
    public void showPlayerTurnTimePickerDialog(Long totalTime) {
        //showTimePickerDialog(totalTime, mOnPlayerTurnTimeSetListener, mOnCancelPlayerTurnTimeSetListener);
        long hour = 0;
        long minute = 0;
        long second = 0;
        if (totalTime != null) {
            hour = TimeUnit.MILLISECONDS.toHours(totalTime);
            minute = TimeUnit.MILLISECONDS.toMinutes(totalTime) - TimeUnit.HOURS.toMinutes(hour);
            second = TimeUnit.MILLISECONDS.toSeconds(totalTime) - TimeUnit.HOURS.toSeconds(hour)
                    - TimeUnit.MINUTES.toSeconds(minute);
        }

        GameDialogBuilder builder = new GameDialogBuilder(this);
        builder.setDialogTitle(R.string.set_time_for_each_turn);

        View contentView = LayoutInflater.from(this).inflate(R.layout.time_picker_dialog, null);
        EditText minuteView = (EditText) contentView.findViewById(R.id.minute_view);
        if (minute > 0) {
            minuteView.setText(String.valueOf(minute));
        }
        EditText secondView = (EditText) contentView.findViewById(R.id.second_view);
        if (second > 0) {
            secondView.setText(String.valueOf(second));
        }

        builder.setContentView(contentView);
        builder.setPositiveButton(R.string.set, dialog -> {
            dialog.dismiss();

            long setMinute = 0;
            long setSecond = 0;
            try {
                if (!TextUtils.isEmpty(minuteView.getText())) {
                    setMinute = Long.parseLong(minuteView.getText().toString());
                }

                if (!TextUtils.isEmpty(secondView.getText())) {
                    setSecond = Long.parseLong(secondView.getText().toString());
                }
            } catch (Exception e) {
                Logger.d("Unable to convert input to long value");
            }

            mPresenter.setPlayerTurnTime(TimeUnit.MINUTES.toMillis(setMinute)
                    + TimeUnit.SECONDS.toMillis(setSecond));
        });

        builder.setNegativeButton(R.string.cancel, DialogInterface::dismiss);

        builder.create().show();
    }

    @Override
    public void showStartingScoreSetting(Boolean enable, Long startingScore) {
        if (mStartingScoreSettingViewHolder == null) {
            mStartingScoreSettingViewHolder = new SettingItemViewHolder();
            settingLayout.addView(mStartingScoreSettingViewHolder.contentView);
        }

        mStartingScoreSettingViewHolder.title.setText(R.string.starting_score);
        if (startingScore == null || startingScore == 0) {
            // Show hint message if the time hasn't been set
            mStartingScoreSettingViewHolder.description.setText(R.string.set_staring_score_for_all_players);
        } else {
            mStartingScoreSettingViewHolder.description.setText(ViewHelper.getFormatScore(startingScore));
        }

        mStartingScoreSettingViewHolder.active.setChecked(enable != null && enable);
        mStartingScoreSettingViewHolder.active.setOnCheckedChangeListener((buttonView, isChecked) ->
            mPresenter.enableStartingScore(isChecked));

        mStartingScoreSettingViewHolder.contentView.setOnClickListener(v -> showStartingScorePickerDialog(startingScore));
    }

    private void showTimePickerDialog(Long totalTime, TimePickerDialog.OnTimeSetListener onTimeSetListener,
                                      DialogInterface.OnCancelListener onCancelListener) {
        long hour = 0;
        long minute = 0;
        long second = 0;
        if (totalTime != null) {
            hour = TimeUnit.MILLISECONDS.toHours(totalTime);
            minute = TimeUnit.MILLISECONDS.toMinutes(totalTime) - TimeUnit.HOURS.toMinutes(hour);
            second = TimeUnit.MILLISECONDS.toSeconds(totalTime) - TimeUnit.HOURS.toSeconds(hour)
                    - TimeUnit.MINUTES.toSeconds(minute);
        }
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, (int)hour, (int)minute, (int)second, true);
        timePickerDialog.enableSeconds(true);
        timePickerDialog.setOnCancelListener(onCancelListener);
        timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
    }

    @Override
    public void showStartingScorePickerDialog(Long score) {
        GameDialogBuilder builder = new GameDialogBuilder(this);
        builder.setDialogTitle(R.string.set_staring_score_for_all_players);
        View contentView = LayoutInflater.from(this).inflate(R.layout.select_starting_score, null);
        EditText scoreInputText = (EditText) contentView.findViewById(R.id.score_input);
        if (score == null) {
            score = 0L;
        }
        scoreInputText.setText(String.valueOf(score));
        FlexboxLayout suggestLayout = (FlexboxLayout) contentView.findViewById(R.id.score_suggest_layout);

        // TODO: provide suggested score

        builder.setContentView(contentView);
        builder.setNegativeButton(R.string.cancel, dialog -> {
            dialog.dismiss();
            mPresenter.onCancelSetStartingScore();
        });

        builder.setPositiveButton(R.string.set, dialog -> {
            dialog.dismiss();
            try {
                long editScore = 0;
                if (!TextUtils.isEmpty(scoreInputText.getText())) {
                    editScore = Long.parseLong(scoreInputText.getText().toString());
                }
                mPresenter.setStartingScore(editScore);
            } catch (Exception e) {
                Logger.e("Failed to set score: " + e.getMessage());
            }
        });

        builder.create().show();
    }

    @Override
    public void showWinningScoreConditionSetting(Integer winningScoreConditionType) {
        if (mWinningScoreConditionSettingViewHolder == null) {
            mWinningScoreConditionSettingViewHolder = new SettingItemViewHolder();
            settingLayout.addView(mWinningScoreConditionSettingViewHolder.contentView);
        }

        mWinningScoreConditionSettingViewHolder.title.setText(R.string.winning_condition);
        int conditionType = winningScoreConditionType == null || winningScoreConditionType == 0 ? 0 : winningScoreConditionType;
        if (conditionType == 0) {
            // default condition: highest score wins the game
            String displayText = getResources().getString(R.string.default_text) + getResources().getString(R.string
                    .highest_score_win);
            mWinningScoreConditionSettingViewHolder.description.setText(displayText);
        } else {
            mWinningScoreConditionSettingViewHolder.description.setText(R.string.lowest_score_win);
        }

        mWinningScoreConditionSettingViewHolder.active.setChecked(conditionType != 0);

        mWinningScoreConditionSettingViewHolder.active.setOnClickListener(v -> mPresenter.toggleWinningScoreCondition());

        mWinningScoreConditionSettingViewHolder.contentView.setOnClickListener(v -> mPresenter
                .toggleWinningScoreCondition());
    }

    @Override
    public void showRollingDiceSetting(Boolean enable, final Integer numberOfRollingDices) {
        if (mRollingDiceSettingViewHolder == null) {
            mRollingDiceSettingViewHolder = new SettingItemViewHolder();
            settingLayout.addView(mRollingDiceSettingViewHolder.contentView);
        }

        mRollingDiceSettingViewHolder.title.setText(R.string.rolling_dice);

        if (numberOfRollingDices != null && numberOfRollingDices != 0) {
            String displayText = ViewHelper.getNumberOfDiceFormat(this, numberOfRollingDices);
            mRollingDiceSettingViewHolder.description.setText(displayText);
        } else {
            mRollingDiceSettingViewHolder.description.setText(R.string.set_number_of_rolling_dice);
        }

        mRollingDiceSettingViewHolder.active.setChecked(enable != null && enable);

        mRollingDiceSettingViewHolder.active.setOnCheckedChangeListener((buttonView, isChecked) -> mPresenter.enableRollingDice(isChecked));

        mRollingDiceSettingViewHolder.contentView.setOnClickListener(v -> showRollingDicePickerDialog(numberOfRollingDices));
    }

    @Override
    public void showRollingDicePickerDialog(Integer numberOfRollingDices) {
        GameDialogBuilder builder = new GameDialogBuilder(this);
        builder.setDialogTitle(R.string.set_number_of_rolling_dice);

        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_dice_setting, null);
        EditText numberOfDiceView = (EditText) contentView.findViewById(R.id.number_of_dice_view);
        if (numberOfRollingDices == null) {
            numberOfRollingDices = 0;
        }
        numberOfDiceView.setText(String.valueOf(numberOfRollingDices));

        builder.setContentView(contentView);
        builder.setPositiveButton(R.string.set, dialog -> {
            dialog.dismiss();
            try {
                int numberOfRollingDice = 0;
                if (!TextUtils.isEmpty(numberOfDiceView.getText())) {
                    numberOfRollingDice = Integer.parseInt(numberOfDiceView.getText().toString());
                }
                mPresenter.setNumberOfRollingDice(numberOfRollingDice);
            } catch (Exception e) {
                Logger.e("Failed to set rolling dice: " + e.getMessage());
            }
        });

        builder.setNegativeButton(R.string.cancel, DialogInterface::dismiss);

        builder.create().show();
    }

    class SettingItemViewHolder {

        View contentView;

        @BindView(R.id.setting_title)
        TextView title;

        @BindView(R.id.setting_description)
        TextView description;

        @BindView(R.id.setting_switch)
        SwitchCompat active;

        public SettingItemViewHolder() {
            contentView = LayoutInflater.from(GameSettingActivity.this).inflate(R.layout.item_setting, settingLayout, false);
            ButterKnife.bind(this, contentView);
        }
    }

}
