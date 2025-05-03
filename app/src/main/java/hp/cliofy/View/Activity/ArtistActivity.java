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

import hp.cliofy.Model.Service.FacadeService;
import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Track;
import hp.cliofy.View.Adapter.AlbumAdapter;
import hp.cliofy.R;
import hp.cliofy.View.Adapter.TrackArtistAdapter;

public class ArtistActivity extends AppCompatActivity {
    private Artist artist;

    private FacadeService facadeService;

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

        facadeService = FacadeService.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            artist = gson.fromJson(bundle.getString("artist"), Artist.class);
            facadeService.hydrateArtist(artist);
        }

        artistImage = findViewById(R.id.artistImage);
        Glide.with(this).load(artist.getImageUrl()).into(artistImage);

        informations = findViewById(R.id.informations);
        informations.setText(String.format("%s\n%d followers\nGenres: %s", artist.getName(), artist.getFollowersTotal(), artist.getGenres()));

        topTracksListView = findViewById(R.id.topTracksListView);
        topTracksList = facadeService.getArtistTopTracks(artist);

        // TODO enlever l'hydratation des activités
        for (Track track : topTracksList) {
            facadeService.hydrateTrack(track);
        }

        TrackArtistAdapter topTracksAdapter = new TrackArtistAdapter(this, topTracksList);
        topTracksListView.setAdapter(topTracksAdapter);
        refreshListViewHeight(topTracksListView);
        topTracksListView.setOnItemClickListener(this::playTrack);

        albumsListView = findViewById(R.id.albumsListView);
        albumsList = facadeService.getArtistAlbums(artist, "album");
        AlbumAdapter albumsAdapter = new AlbumAdapter(this, albumsList);
        albumsListView.setAdapter(albumsAdapter);
        refreshListViewHeight(albumsListView);
        albumsListView.setOnItemClickListener(this::openAlbumActivity);

        singlesListView = findViewById(R.id.singlesListView);
        singlesList = facadeService.getArtistAlbums(artist, "single");
        AlbumAdapter singlesAdapter = new AlbumAdapter(this, singlesList);
        singlesListView.setAdapter(singlesAdapter);
        refreshListViewHeight(singlesListView);
        singlesListView.setOnItemClickListener(this::openSingleActivity);

        compilationsListView = findViewById(R.id.compilationsListView);
        compilationsList = facadeService.getArtistAlbums(artist, "compilation");
        AlbumAdapter compilationsAdapter = new AlbumAdapter(this, compilationsList);
        compilationsListView.setAdapter(compilationsAdapter);
        refreshListViewHeight(compilationsListView);
        compilationsListView.setOnItemClickListener(this::openCompilationActivity);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Prevent sleep mode
    }

    private void playTrack(AdapterView<?> adapterView, View view, int i, long l) {
        facadeService.play(topTracksList.get(i).getUri());
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