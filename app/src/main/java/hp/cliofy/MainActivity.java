package hp.cliofy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity of the project
 */
public class MainActivity extends AppCompatActivity implements IObserver {
    /**
     * General DAO communicating with the different Spotify APIs
     */
    private GeneralDAO generalDAO;

    /**
     * pause/resume button
     */
    private Button pauseResumeButton;

    /**
     * skip to the previous track button
     */
    private Button skipPreviousButton;

    /**
     * skip to the next track button
     */
    private Button skipNextButton;

    /**
     * shuffling mode switch
     */
    private Switch shuffleSwitch;

    /**
     * Informations of the current track
     */
    private TextView informations;

    /**
     * current album cover
     */
    private ImageView albumCover;

    /**
     * listview contenant les playlists (vue)
     */
    private ListView playlistsListView;

    /**
     * liste contenant les playlists (modèle)
     */
    private List<Playlist> playlistsList;

    /**
     * TODO expliquer
     */
    private ArrayAdapter<Playlist> adapter;

    // TODO commenter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        skipPreviousButton = findViewById(R.id.skipPreviousButton);
        skipNextButton = findViewById(R.id.skipNextButton);
        shuffleSwitch = findViewById(R.id.shuffleSwitch);
        informations = findViewById(R.id.informations);
        albumCover = findViewById(R.id.albumCover);
        playlistsListView = findViewById(R.id.playlistsListView);
        pauseResumeButton.setOnClickListener(this::pauseResumeClick);
        skipPreviousButton.setOnClickListener(this::skipPreviousClick);
        skipNextButton.setOnClickListener(this::skipNextClick);
        shuffleSwitch.setOnClickListener(this::shuffleClick);

        playlistsList = new ArrayList<Playlist>();
        adapter = new ArrayAdapter<Playlist>(this, android.R.layout.simple_list_item_1, playlistsList);
        playlistsListView.setAdapter(adapter);
        playlistsListView.setOnItemClickListener(this::playPlaylist);

        generalDAO = new GeneralDAO();
        generalDAO.addObserver(this);
        generalDAO.connect(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Plays a playlist depending on its index in the list
     * @param adapterView TODO expliquer
     * @param view TODO expliquer
     * @param i index of the playlist in the list
     * @param l TODO expliquer
     */
    private void playPlaylist(AdapterView<?> adapterView, View view, int i, long l) {
        generalDAO.play(playlistsList.get(i).getUri());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // TODO enlever ?
    @Override
    protected void onStop() {
        super.onStop();
    }

    // TODO enlever ?
    @Override
    protected void onDestroy() {
        super.onDestroy();
        generalDAO.removeObserver(this);
        generalDAO.disconnect();
    }

    // TODO commenter
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith("com.hp.cliofy://callback")) {
            String authorizationCode = uri.getQueryParameter("code");
            if (authorizationCode != null) {
                Toast.makeText(this, "Connecté à l'API", Toast.LENGTH_SHORT).show();
                generalDAO.storeAuthorizationCode(authorizationCode);
                List<Playlist> list = generalDAO.getPlaylistsList();
                for (Playlist playlist : list) {
                    playlistsList.add(playlist);
                }
            }
            else {
                Toast.makeText(this, "Erreur lors de la connexion à l'API : connexion refusée", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Erreur lors de la connexion à l'API : mauvaise redirection", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Function called when the pause/resume button is clicked
     * @param v TODO expliquer
     */
    private void pauseResumeClick(View v) {
        generalDAO.pauseResume();
    }

    /**
     * Function called when the skip to previous track button is clicked
     * @param v TODO expliquer
     */
    private void skipPreviousClick(View v) {
        generalDAO.skipPrevious();
    }

    /**
     * Function called when the skip to next track is clicked
     * @param v TODO expliquer
     */
    private void skipNextClick(View v) {
        generalDAO.skipNext();
    }

    /**
     * Function called when the shuffle switch is clicked
     * @param v TODO expliquer
     */
    private void shuffleClick(View v) {
        if (shuffleSwitch.isChecked()) {
            generalDAO.enableShuffle();
        }
        else {
            generalDAO.disableShuffle();
        }
    }

    @Override
    public void pauseChange(boolean isPaused) {
        if (isPaused) {
            pauseResumeButton.setText("Resume");
        }
        else {
            pauseResumeButton.setText("Pause");
        }
    }

    @Override
    public void shuffleChange(boolean isShuffling) {
        shuffleSwitch.setChecked(isShuffling);
    }

    @Override
    public void trackChange(Track track) {
        informations.setText(
                track.name + "\n" +
                track.artist.name + "\n" +
                track.album.name
        );
    }

    @Override
    public void imageChange(Bitmap bitmap) {
        albumCover.setImageBitmap(bitmap);
    }
}