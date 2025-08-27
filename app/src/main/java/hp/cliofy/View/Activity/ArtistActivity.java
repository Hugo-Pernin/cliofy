package hp.cliofy.View.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
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

import java.util.ArrayList;
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

        loadLoadingIcon(R.id.loading_top_tracks);
        loadLoadingIcon(R.id.loading_albums);
        loadLoadingIcon(R.id.loading_singles);
        loadLoadingIcon(R.id.loading_compilations);

        facadeService = FacadeService.getInstance(this);

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
        topTracksList = new ArrayList<>();
        TrackArtistAdapter topTracksAdapter = new TrackArtistAdapter(this, topTracksList);
        topTracksListView.setAdapter(topTracksAdapter);
        topTracksListView.setOnItemClickListener(this::playTrack);

        albumsListView = findViewById(R.id.albumsListView);
        albumsList = new ArrayList<>();
        AlbumAdapter albumsAdapter = new AlbumAdapter(this, albumsList);
        albumsListView.setAdapter(albumsAdapter);
        albumsListView.setOnItemClickListener(this::openAlbumActivity);

        singlesListView = findViewById(R.id.singlesListView);
        singlesList = new ArrayList<>();
        AlbumAdapter singlesAdapter = new AlbumAdapter(this, singlesList);
        singlesListView.setAdapter(singlesAdapter);
        singlesListView.setOnItemClickListener(this::openSingleActivity);

        compilationsListView = findViewById(R.id.compilationsListView);
        compilationsList = new ArrayList<>();
        AlbumAdapter compilationsAdapter = new AlbumAdapter(this, compilationsList);
        compilationsListView.setAdapter(compilationsAdapter);
        compilationsListView.setOnItemClickListener(this::openCompilationActivity);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Prevent sleep mode

        facadeService.getArtistTopTracks(artist).thenAccept(result -> {
            runOnUiThread(() -> {
                for (Track track : result) {
                    facadeService.hydrateTrack(track);
                    topTracksList.add(track);
                }
                topTracksAdapter.notifyDataSetChanged();
                refreshListViewHeight(topTracksListView);
                hideView(R.id.loading_top_tracks);
            });
        });

        facadeService.getArtistAlbums(artist, "album").thenAccept(result -> {
            runOnUiThread(() -> {
                albumsList.addAll(result);
                albumsAdapter.notifyDataSetChanged();
                refreshListViewHeight(albumsListView);
                hideView(R.id.loading_albums);
            });
        });

        facadeService.getArtistAlbums(artist, "single").thenAccept(result -> {
            runOnUiThread(() -> {
                singlesList.addAll(result);
                singlesAdapter.notifyDataSetChanged();
                refreshListViewHeight(singlesListView);
                hideView(R.id.loading_singles);
            });
        });

        facadeService.getArtistAlbums(artist, "compilation").thenAccept(result -> {
            runOnUiThread(() -> {
                compilationsList.addAll(result);
                compilationsAdapter.notifyDataSetChanged();
                refreshListViewHeight(compilationsListView);
                hideView(R.id.loading_compilations);
            });
        });
    }

    private void loadLoadingIcon(@IdRes int id) {
        ImageView loading = findViewById(id);
        Glide.with(this).asGif().load("file:///android_asset/loading.gif").into(loading);
    }

    private void hideView(@IdRes int id) {
        View view = findViewById(id);
        view.setVisibility(View.GONE);
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