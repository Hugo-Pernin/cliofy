package hp.cliofy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        artistImage.setImageBitmap(generalDAO.getBitmapImageFromUrl(artist.getImageUrl()));

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
        albumsList = generalDAO.getArtistAlbums(artist, "album");
        ArrayAdapter<Album> albumsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albumsList);
        albumsListView.setAdapter(albumsAdapter);
        refreshListViewHeight(albumsListView);
        albumsListView.setOnItemClickListener(this::openAlbumActivity);

        singlesListView = findViewById(R.id.singlesListView);
        singlesList = generalDAO.getArtistAlbums(artist, "single");
        ArrayAdapter<Album> singlesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, singlesList);
        singlesListView.setAdapter(singlesAdapter);
        refreshListViewHeight(singlesListView);
        singlesListView.setOnItemClickListener(this::openSingleActivity);

        compilationsListView = findViewById(R.id.compilationsListView);
        compilationsList = generalDAO.getArtistAlbums(artist, "compilation");
        ArrayAdapter<Album> compilationsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, compilationsList);
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