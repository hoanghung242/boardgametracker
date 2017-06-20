package ghost.gboardgametracker.ui.gamedetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ghost.gboardgametracker.R;
import ghost.gboardgametracker.ui.base.BaseActivity;
import ghost.gboardgametracker.ui.gamedetail.model.GameDetailUIModel;
import ghost.gboardgametracker.ui.newgame.NewGameActivity;
import ghost.gboardgametracker.ui.play.GamePlayActivity;
import ghost.gboardgametracker.utils.AppConstants;

import javax.inject.Inject;

public class GameDetailActivity extends BaseActivity implements GameDetailMvpView{

    private static final String EXTRA_KEY_GAME_ID = "game_id";

    @Inject
    GameDetailMvpPresenter<GameDetailMvpView> mPresenter;

    private GameDetailUIModel mGameDetailUIModel;

    public static void start(Context context, Long gameId) {
        Intent starter = new Intent(context, GameDetailActivity.class);
        starter.putExtra(EXTRA_KEY_GAME_ID, gameId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mGameDetailUIModel = new GameDetailUIModel();
        Long gameId = getIntent().getLongExtra(EXTRA_KEY_GAME_ID, AppConstants.NULL_ID);
        mPresenter.loadGameInfo(gameId);
    }

    @Override
    protected void componentInjection() {
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
    }

    @Override
    public void displayGameInfo(GameDetailUIModel gameDetailUIModel) {
        mGameDetailUIModel = gameDetailUIModel;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(gameDetailUIModel.title);
        }
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

    @OnClick(R.id.start_btn)
    void onClickStartBtn() {
        GamePlayActivity.start(this, mGameDetailUIModel.gameId);
        finish();
    }

    @OnClick(R.id.edit_btn)
    void onClickEditBtn() {
        NewGameActivity.start(this, mGameDetailUIModel.gameId);
        finish();
    }
}
