package hp.cliofy.View.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

import hp.cliofy.Model.DAO.GeneralDAO;
import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Track;
import hp.cliofy.View.Adapter.ItemAdapter;
import hp.cliofy.R;

public class ArtistActivity extends AppCompatActivity {
    private Artist artist;

    private GeneralDAO generalDAO;

    private ImageView artistImage;

    private TextView informations;

    private ListView topTracksListView;
    private List<Track> topTracksList;

    private ListView albumsListView;
    private List<Album> albumsList;

    private ListView singlesListView;
    private List<Album> singlesList;

    private ListView compilationsListView;
    private List<Album> compilationsList;

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

        artistImage = findViewById(R.id.artistImage);
        Glide.with(this).load(artist.getImageUrl()).into(artistImage);

        informations = findViewById(R.id.informations);
        informations.setText(artist.toString() + "\n" +
                artist.getFollowersTotal() + " followers\n" +
                "Genres: " + artist.getGenres());

        topTracksListView = findViewById(R.id.topTracksListView);
        topTracksList = generalDAO.getArtistTopTracks(artist);

        // TODO enlever l'hydratation des activités
        for (Track track : topTracksList) {
            generalDAO.hydrateTrack(track);
        }

        ItemAdapter<Track> topTracksAdapter = new ItemAdapter<>(this, topTracksList);
        topTracksListView.setAdapter(topTracksAdapter);
        refreshListViewHeight(topTracksListView);
        topTracksListView.setOnItemClickListener(this::playTrack);

        albumsListView = findViewById(R.id.albumsListView);
        albumsList = generalDAO.getArtistAlbums(artist, "album");
        ItemAdapter<Album> albumsAdapter = new ItemAdapter<>(this, albumsList);
        albumsListView.setAdapter(albumsAdapter);
        refreshListViewHeight(albumsListView);
        albumsListView.setOnItemClickListener(this::openAlbumActivity);

        singlesListView = findViewById(R.id.singlesListView);
        singlesList = generalDAO.getArtistAlbums(artist, "single");
        ItemAdapter<Album> singlesAdapter = new ItemAdapter<>(this, singlesList);
        singlesListView.setAdapter(singlesAdapter);
        refreshListViewHeight(singlesListView);
        singlesListView.setOnItemClickListener(this::openSingleActivity);

        compilationsListView = findViewById(R.id.compilationsListView);
        compilationsList = generalDAO.getArtistAlbums(artist, "compilation");
        ItemAdapter<Album> compilationsAdapter = new ItemAdapter<>(this, compilationsList);
        compilationsListView.setAdapter(compilationsAdapter);
        refreshListViewHeight(compilationsListView);
        compilationsListView.setOnItemClickListener(this::openCompilationActivity);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Prevent sleep mode
    }

    private void playTrack(AdapterView<?> adapterView, View view, int i, long l) {
        generalDAO.play(topTracksList.get(i).getUri());
    }

    // TODO refactoriser
    private void openAlbumActivity(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, AlbumActivity.class);
        Album album = albumsList.get(i);
        Gson gson = new Gson();
        intent.putExtra("album", gson.toJson(album));
        startActivity(intent);
    }

    private void openSingleActivity(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, AlbumActivity.class);
        Album album = singlesList.get(i);
        Gson gson = new Gson();
        intent.putExtra("album", gson.toJson(album));
        startActivity(intent);
    }

    private void openCompilationActivity(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, AlbumActivity.class);
        Album album = compilationsList.get(i);
        Gson gson = new Gson();
        intent.putExtra("album", gson.toJson(album));
        startActivity(intent);
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