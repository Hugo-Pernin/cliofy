package hp.cliofy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import hp.cliofy.DAO.GeneralDAO;
import hp.cliofy.Item.Album;
import hp.cliofy.Item.Artist;
import hp.cliofy.Item.Track;

public class ArtistActivity extends AppCompatActivity {
    private Artist artist;

    private GeneralDAO generalDAO;

    private TextView informations;

    private ListView topTracksListView;
    private List<Track> topTracksList;

    private ListView albumsListView;
    private List<Album> albumsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        generalDAO = GeneralDAO.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            artist = gson.fromJson(bundle.getString("artist"), Artist.class);
            generalDAO.hydrateArtist(artist);
        }

        informations = findViewById(R.id.informations);
        informations.setText(artist.toString() + "\n" +
                artist.getFollowersTotal() + " followers\n" +
                "Genres: " + artist.getGenres());

        topTracksListView = findViewById(R.id.topTracksListView);
        topTracksList = generalDAO.getArtistTopTracks(artist);
        ArrayAdapter<Track> topTracksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topTracksList);
        topTracksListView.setAdapter(topTracksAdapter);
        refreshListViewHeight(topTracksListView);
        topTracksListView.setOnItemClickListener(this::playTrack);

        albumsListView = findViewById(R.id.albumsListView);
        albumsList = generalDAO.getArtistAlbums(artist);
        ArrayAdapter<Album> albumsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albumsList);
        albumsListView.setAdapter(albumsAdapter);
        refreshListViewHeight(albumsListView);
        albumsListView.setOnItemClickListener(this::playAlbum);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Prevent sleep mode
    }

    private void playTrack(AdapterView<?> adapterView, View view, int i, long l) {
        generalDAO.play(topTracksList.get(i).getUri());
    }

    private void playAlbum(AdapterView<?> adapterView, View view, int i, long l) {
        generalDAO.play(albumsList.get(i).getUri());
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