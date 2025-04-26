package hp.cliofy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;

import hp.cliofy.DAO.GeneralDAO;
import hp.cliofy.Item.Artist;

public class ArtistActivity extends AppCompatActivity {
    private Artist artist;

    private GeneralDAO generalDAO;

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

        TextView textView = findViewById(R.id.textView);
        textView.setText(artist.toString() + "\n" +
                artist.getFollowersTotal() + " followers\n" +
                "Genres: " + artist.getGenres());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Prevent sleep mode
    }
}