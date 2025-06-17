package hp.cliofy.View.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import hp.cliofy.Model.Service.FacadeService;
import hp.cliofy.Model.Item.Playlist;
import hp.cliofy.Model.Item.Track;
import hp.cliofy.R;
import hp.cliofy.View.Adapter.TrackPlaylistAdapter;

public class PlaylistActivity extends AppCompatActivity {
    private Playlist playlist;
    private FacadeService facadeService;
    private ImageView playlistCover;
    private TextView informations;
    private ListView tracksListView;
    private List<Track> tracksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        loadLoadingIcon(R.id.loading);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            playlist = gson.fromJson(bundle.getString("playlist"), Playlist.class);
        }

        playlistCover = findViewById(R.id.playlistCover);
        Glide.with(this).load(playlist.getImageUrl()).into(playlistCover);

        informations = findViewById(R.id.informations);
        informations.setText(String.format("%s by %s", playlist.getName(), playlist.getOwner()));

        tracksListView = findViewById(R.id.tracksListView);
        tracksList = new ArrayList<>();

        TrackPlaylistAdapter tracksAdapter = new TrackPlaylistAdapter(this, tracksList);
        tracksListView.setAdapter(tracksAdapter);
        tracksListView.setOnItemClickListener(this::playTrack);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Prevent sleep mode

        facadeService = FacadeService.getInstance();

        facadeService.getPlaylistTracks(playlist).thenAccept(result -> {
            runOnUiThread(() -> {
                for (Track track : result) {
                    facadeService.hydrateTrack(track);
                    tracksList.add(track);
                }
                tracksAdapter.notifyDataSetChanged();
                refreshListViewHeight(tracksListView);
                hideView(R.id.loading);
            });
        });
    }

    private void loadLoadingIcon(@IdRes int id) {
        ImageView loading = findViewById(id);
        Glide.with(this).asGif().load("file:///android_asset/loading.gif").into(loading);
    }

    private void hideView(@IdRes int id) {
        View view = findViewById(id);
        view.setVisibility(View.GONE);
    }

    private void playTrack(AdapterView<?> adapterView, View view, int i, long l) {
        facadeService.playWithOffset(playlist.getUri(), i);
    }

    private void refreshListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            totalHeight += listItem.getMeasuredHeight();
        }
        listView.getLayoutParams().height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    }
}