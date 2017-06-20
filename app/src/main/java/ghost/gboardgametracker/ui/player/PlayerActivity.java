package ghost.gboardgametracker.ui.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ghost.gboardgametracker.R;
import ghost.gboardgametracker.data.db.model.Player;
import ghost.gboardgametracker.ui.base.BaseDrawerActivity;
import ghost.gboardgametracker.ui.navigation.NavigationItemEnum;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends BaseDrawerActivity implements PlayerMvpView{

    @Inject
    PlayerMvpPresenter<PlayerMvpView> mPresenter;

    @BindView(R.id.player_list_view)
    RecyclerView mPlayerListView;

    private List<Player> mPlayerList = new ArrayList<>();
    private PlayerAdapter mPlayerAdapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, PlayerActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ButterKnife.bind(this);

        setupView();

        mPresenter.onViewLoaded();
    }

    @Override
    protected void componentInjection() {
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
    }

    @Override
    protected void setupActionBar() {
        super.setupActionBar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.players);
        }
    }

    @Override
    protected NavigationItemEnum getSelfNavigationItem() {
        // TODO: enable in next version
        // return NavigationItemEnum.PLAYER;
        return NavigationItemEnum.HOME;
    }

    @Override
    public void refreshPlayerList(List<Player> playerList) {
        mPlayerList.clear();
        if (playerList != null) {
            mPlayerList.addAll(playerList);
        }
        mPlayerAdapter.notifyDataSetChanged();
    }

    private void setupView() {
        mPlayerListView.setLayoutManager(new LinearLayoutManager(this));
        mPlayerListView.setHasFixedSize(true);
        mPlayerAdapter = new PlayerAdapter();
        mPlayerListView.setAdapter(mPlayerAdapter);
    }

    class PlayerViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.player_name)
        TextView nameTextView;

        @BindView(R.id.player_game_id)
        TextView gameIdTextView;

        public PlayerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class PlayerAdapter extends RecyclerView.Adapter<PlayerViewHolder>{

        @Override
        public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(PlayerActivity.this).inflate(R.layout.item_player_info, parent, false);
            return new PlayerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PlayerViewHolder holder, int position) {
            Player player = mPlayerList.get(position);
            holder.nameTextView.setText(player.getName());
        }

        @Override
        public int getItemCount() {
            return mPlayerList.size();
        }
    }
}
