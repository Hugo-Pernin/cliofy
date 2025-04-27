package hp.cliofy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.WindowManager;
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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            artist = gson.fromJson(bundle.getString("artist"), Artist.class);
        }

        generalDAO = GeneralDAO.getInstance();

        informations = findViewById(R.id.informations);
        informations.setText(artist.toString() + "\n" +
                artist.getFollowersTotal() + " followers\n" +
                "Genres: " + artist.getGenres());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Prevent sleep mode
    }
}