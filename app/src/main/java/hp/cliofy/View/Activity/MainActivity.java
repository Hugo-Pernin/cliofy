package hp.cliofy.View.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import hp.cliofy.Model.ObserverAuthentication.IObserverAuthentication;
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
public class MainActivity extends AppCompatActivity implements IObserver, IObserverAuthentication {
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
     * shuffling mode button
     */
    private Button shuffleButton;

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

    private PlaylistAdapter playlistsAdapter;
    private ArtistAdapter topArtistsAdapter;

    // TODO commenter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadLoadingIcon(R.id.loading_playlists);
        loadLoadingIcon(R.id.loading_artists);

        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        skipPreviousButton = findViewById(R.id.skipPreviousButton);
        skipNextButton = findViewById(R.id.skipNextButton);
        shuffleButton = findViewById(R.id.shuffleButton);
        informations = findViewById(R.id.informations);
        albumCover = findViewById(R.id.albumCover);
        playlistsListView = findViewById(R.id.playlistsListView);
        topArtistsListView = findViewById(R.id.topArtistsListView);

        pauseResumeButton.setOnClickListener(this::pauseResumeClick);
        skipPreviousButton.setOnClickListener(this::skipPreviousClick);
        skipNextButton.setOnClickListener(this::skipNextClick);
        shuffleButton.setOnClickListener(this::shuffleClick);

        playlistsList = new ArrayList<>();
        playlistsAdapter = new PlaylistAdapter(this, playlistsList);
        playlistsListView.setAdapter(playlistsAdapter);
        playlistsListView.setOnItemClickListener(this::openPlaylistActivity);

        topArtistsList = new ArrayList<>();
        topArtistsAdapter = new ArtistAdapter(this, topArtistsList);
        topArtistsListView.setAdapter(topArtistsAdapter);
        topArtistsListView.setOnItemClickListener(this::openArtistActivity);

        facadeService = FacadeService.getInstance(this);
        facadeService.addObserver(this);
        facadeService.connect(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Prevent sleep mode
    }

    private void loadLoadingIcon(@IdRes int id) {
        ImageView loading = findViewById(id);
        Glide.with(this).asGif().load("file:///android_asset/loading.gif").into(loading);
    }

    private void hideView(@IdRes int id) {
        View view = findViewById(id);
        view.setVisibility(View.GONE);
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
                facadeService.requestAccessToken(authorizationCode, this);
                facadeService.connectAndroidSDKDAO(this);

                facadeService.getPlaylistsList().thenAccept(result -> {
                    runOnUiThread(() -> {
                        playlistsList.addAll(result);
                        playlistsAdapter.notifyDataSetChanged();
                        refreshListViewHeight(playlistsListView);
                        hideView(R.id.loading_playlists);
                    });
                });

                facadeService.getTopArtists().thenAccept(result -> {
                    runOnUiThread(() -> {
                        topArtistsList.addAll(result);
                        topArtistsAdapter.notifyDataSetChanged();
                        refreshListViewHeight(topArtistsListView);
                        hideView(R.id.loading_artists);
                    });
                });
            }
            else {
                Toast.makeText(this, "Erreur lors de la connexion à l'API : connexion refusée", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Erreur lors de la connexion à l'API : mauvaise redirection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void accessTokenRefreshed() {
        facadeService.connectAndroidSDKDAO(this);

        Toast.makeText(this, "Jeton rafraichi", Toast.LENGTH_SHORT).show();

        facadeService.getPlaylistsList().thenAccept(result -> {
            runOnUiThread(() -> {
                playlistsList.addAll(result);
                playlistsAdapter.notifyDataSetChanged();
                refreshListViewHeight(playlistsListView);
                hideView(R.id.loading_playlists);
            });
        });

        facadeService.getTopArtists().thenAccept(result -> {
            runOnUiThread(() -> {
                topArtistsList.addAll(result);
                topArtistsAdapter.notifyDataSetChanged();
                refreshListViewHeight(topArtistsListView);
                hideView(R.id.loading_artists);
            });
        });
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
        facadeService.shuffleSwitch();
    }

    // TODO utiliser les ressources
    @Override
    public void pauseChange(boolean isPaused) {
        Drawable drawable;
        String contentDescription;
        if (isPaused) {
            drawable = ContextCompat.getDrawable(this, R.drawable.play_fill);
            contentDescription = getResources().getString(R.string.resume);
        }
        else {
            drawable = ContextCompat.getDrawable(this, R.drawable.pause_fill);
            contentDescription = getResources().getString(R.string.pause);
        }
        pauseResumeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null);
        pauseResumeButton.setContentDescription(contentDescription);
    }

    @Override
    public void shuffleChange(boolean isShuffling) {
        float alpha;
        String contentDescription;
        if (isShuffling) {
            alpha = 1.0f;
            contentDescription = "Disable shuffling";
        }
        else {
            alpha = 0.5f;
            contentDescription = "Enable shuffling";
        }
        shuffleButton.setAlpha(alpha);
        shuffleButton.setContentDescription(contentDescription);
    }

    @Override
    public void trackChange(Track track) {
        facadeService.hydrateTrack(track);
        informations.setText(String.format("%s\n%s\n%s", track.getName(), track.getArtist().getName(), track.getAlbum().getName()));
        albumCover.setImageBitmap(facadeService.getBitmapImageFromUrl(track.getImageUrl()));
    }

    @Override
    public void imageChange(Bitmap bitmap) {
        albumCover.setImageBitmap(bitmap);
    }
}