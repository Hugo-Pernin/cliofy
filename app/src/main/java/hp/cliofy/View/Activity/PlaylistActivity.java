package hp.cliofy.View.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

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

        facadeService = FacadeService.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            playlist = gson.fromJson(bundle.getString("playlist"), Playlist.class);
        }

        playlistCover = findViewById(R.id.playlistCover);
        Glide.with(this).load(playlist.getImageUrl()).into(playlistCover);

        informations = findViewById(R.id.informations);
        informations.setText(playlist.getName() + " by " + playlist.getOwner());

        tracksListView = findViewById(R.id.tracksListView);
        tracksList = facadeService.getPlaylistTracks(playlist);

        // TODO enlever l'hydratation des activités
        for (Track track : tracksList) {
            facadeService.hydrateTrack(track);
        }

        TrackPlaylistAdapter tracksAdapter = new TrackPlaylistAdapter(this, tracksList);
        tracksListView.setAdapter(tracksAdapter);
        refreshListViewHeight(tracksListView);
        tracksListView.setOnItemClickListener(this::playTrack);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Prevent sleep mode
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