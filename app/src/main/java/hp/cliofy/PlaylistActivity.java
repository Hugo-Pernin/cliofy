package hp.cliofy;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

import hp.cliofy.DAO.GeneralDAO;
import hp.cliofy.Item.Playlist;
import hp.cliofy.Item.Track;

public class PlaylistActivity extends AppCompatActivity {
    private Playlist playlist;
    private GeneralDAO generalDAO;
    private ImageView playlistCover;
    private TextView informations;
    private ListView tracksListView;
    private List<Track> tracksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        generalDAO = GeneralDAO.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            playlist = gson.fromJson(bundle.getString("playlist"), Playlist.class);
            generalDAO.hydratePlaylist(playlist);
        }

        playlistCover = findViewById(R.id.playlistCover);
        Glide.with(this).load(playlist.getImageUrl()).into(playlistCover);

        informations = findViewById(R.id.informations);
        informations.setText(playlist.toString() + " by " + playlist.getOwner());

        tracksListView = findViewById(R.id.tracksListView);
        tracksList = generalDAO.getPlaylistTracks(playlist);

        // TODO enlever l'hydratation des activités
        for (Track track : tracksList) {
            generalDAO.hydrateTrack(track);
        }

        //ArrayAdapter<Track> tracksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tracksList);
        ItemAdapter<Track> tracksAdapter = new ItemAdapter<>(this, tracksList);
        tracksListView.setAdapter(tracksAdapter);
        refreshListViewHeight(tracksListView);
        tracksListView.setOnItemClickListener(this::playTrack);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Prevent sleep mode
    }

    private void playTrack(AdapterView<?> adapterView, View view, int i, long l) {
        generalDAO.playWithOffset(playlist.getUri(), i);
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