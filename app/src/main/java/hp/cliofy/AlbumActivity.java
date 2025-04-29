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
import hp.cliofy.Item.Album;
import hp.cliofy.Item.Track;

public class AlbumActivity extends AppCompatActivity {
    private Album album;
    private GeneralDAO generalDAO;
    private ImageView albumCover;
    private TextView informations;
    private ListView tracksListView;
    private List<Track> tracksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        generalDAO = GeneralDAO.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            album = gson.fromJson(bundle.getString("album"), Album.class);
            generalDAO.hydrateAlbum(album);
        }

        albumCover = findViewById(R.id.albumCover);
        Glide.with(this).load(album.getImageUrl()).into(albumCover);

        informations = findViewById(R.id.informations);
        informations.setText(album.toString() + "\n" +
                "Type: " + album.getAlbumType() + "\n" +
                album.getTotalTracks() + " tracks\n" +
                album.getReleaseDate());

        tracksListView = findViewById(R.id.tracksListView);
        tracksList = generalDAO.getAlbumTracks(album);

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
        generalDAO.playWithOffset(album.getUri(), i);
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