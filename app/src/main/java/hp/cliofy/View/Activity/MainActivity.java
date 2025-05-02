package hp.cliofy.View.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import hp.cliofy.Model.Service.FacadeService;
import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Playlist;
import hp.cliofy.Model.Item.Track;
import hp.cliofy.View.Adapter.ArtistAdapter;
import hp.cliofy.Model.Observer.IObserver;
import hp.cliofy.R;
import hp.cliofy.View.Adapter.PlaylistAdapter;

/**
 * Main activity of the project
 */
public class MainActivity extends AppCompatActivity implements IObserver {
    /**
     * General DAO communicating with the different Spotify APIs
     */
    private FacadeService facadeService;

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

    private ListView topArtistsListView;
    private List<Artist> topArtistsList;

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
        topArtistsListView = findViewById(R.id.topArtistsListView);

        pauseResumeButton.setOnClickListener(this::pauseResumeClick);
        skipPreviousButton.setOnClickListener(this::skipPreviousClick);
        skipNextButton.setOnClickListener(this::skipNextClick);
        shuffleSwitch.setOnClickListener(this::shuffleClick);

        playlistsList = new ArrayList<>();
        PlaylistAdapter playlistsAdapter = new PlaylistAdapter(this, playlistsList);
        playlistsListView.setAdapter(playlistsAdapter);
        playlistsListView.setOnItemClickListener(this::openPlaylistActivity);

        topArtistsList = new ArrayList<>();
        ArtistAdapter topArtistsAdapter = new ArtistAdapter(this, topArtistsList);
        topArtistsListView.setAdapter(topArtistsAdapter);
        topArtistsListView.setOnItemClickListener(this::openArtistActivity);

        facadeService = FacadeService.getInstance();
        facadeService.addObserver(this);
        facadeService.connect(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Prevent sleep mode
    }

    /**
     * Plays a playlist depending on its index in the list
     * @param adapterView TODO expliquer
     * @param view TODO expliquer
     * @param i index of the playlist in the list
     * @param l TODO expliquer
     */
    private void openPlaylistActivity(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, PlaylistActivity.class);
        Playlist playlist = playlistsList.get(i);
        Gson gson = new Gson();
        intent.putExtra("playlist", gson.toJson(playlist));
        startActivity(intent);
    }

    /**
     * Opens an ArtistActivity of the artist depending on its index in the list
     * @param adapterView TODO expliquer
     * @param view TODO expliquer
     * @param i index of the artist in the list
     * @param l TODO expliquer
     */
    private void openArtistActivity(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, ArtistActivity.class);
        Artist artist = topArtistsList.get(i);
        Gson gson = new Gson();
        intent.putExtra("artist", gson.toJson(artist));
        startActivity(intent);
    }

    // TODO enlever ?
    @Override
    protected void onStart() {
        super.onStart();
    }

    // TODO enlever ?
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        facadeService.removeObserver(this);
        facadeService.disconnect();
    }

    // TODO commenter
    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith("com.hp.cliofy://callback")) {
            String authorizationCode = uri.getQueryParameter("code");
            if (authorizationCode != null) {
                Toast.makeText(this, "Connecté à l'API", Toast.LENGTH_SHORT).show();
                facadeService.storeAuthorizationCode(authorizationCode, this);
                List<Playlist> playlists = facadeService.getPlaylistsList();
                playlistsList.addAll(playlists);
                List<Artist> topArtists = facadeService.getTopArtists();
                topArtistsList.addAll(topArtists);
                refreshListViewHeight(playlistsListView);
                refreshListViewHeight(topArtistsListView);
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
     * Refreshes a ListView's height based on its children
     * @param listView ListView to refresh
     */
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

    /**
     * Function called when the pause/resume button is clicked
     * @param v TODO expliquer
     */
    private void pauseResumeClick(View v) {
        facadeService.pauseResume();
    }

    /**
     * Function called when the skip to previous track button is clicked
     * @param v TODO expliquer
     */
    private void skipPreviousClick(View v) {
        facadeService.skipPrevious();
    }

    /**
     * Function called when the skip to next track is clicked
     * @param v TODO expliquer
     */
    private void skipNextClick(View v) {
        facadeService.skipNext();
    }

    /**
     * Function called when the shuffle switch is clicked
     * @param v TODO expliquer
     */
    private void shuffleClick(View v) {
        if (shuffleSwitch.isChecked()) {
            facadeService.enableShuffle();
        }
        else {
            facadeService.disableShuffle();
        }
    }

    // TODO utiliser les ressources
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
        facadeService.hydrateTrack(track);
        informations.setText(
                track.toString() + "\n" +
                track.getArtist().toString() + "\n" +
                track.getAlbum().toString()
        );
        facadeService.hydrateAlbum(track.getAlbum());
        albumCover.setImageBitmap(facadeService.getBitmapImageFromUrl(track.getImageUrl()));
    }

    @Override
    public void imageChange(Bitmap bitmap) {
        albumCover.setImageBitmap(bitmap);
    }
}