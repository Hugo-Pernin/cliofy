package hp.cliofy.View.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Track;
import hp.cliofy.Model.Service.FacadeService;
import hp.cliofy.R;
import hp.cliofy.View.Activity.AlbumActivity;

public class TrackPlaylistAdapter extends ArrayAdapter<Track> {
    public TrackPlaylistAdapter(Context context, List<Track> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Track track = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_track_playlist, parent, false);
        }

        ImageView albumImage = convertView.findViewById(R.id.albumImage);
        TextView trackName = convertView.findViewById(R.id.trackName);
        Button queueButton = convertView.findViewById(R.id.queueButton);
        Button albumButton = convertView.findViewById(R.id.albumButton);
        Button artistButton = convertView.findViewById(R.id.artistButton);

        Glide.with(this.getContext()).load(track.getImageUrl()).into(albumImage);
        trackName.setText(track.getName() + " - " + track.getArtist().getName());
        queueButton.setOnClickListener((View view) -> {
            FacadeService.getInstance().addItemToPlaybackQueue(track.getUri());
        });
        albumButton.setOnClickListener((View view) -> {
            // TODO respecter S & O
            Intent intent = new Intent(getContext(), AlbumActivity.class);
            Album album = track.getAlbum();
            Gson gson = new Gson();
            intent.putExtra("album", gson.toJson(album));
            getContext().startActivity(intent);
        });
        artistButton.setOnClickListener((View view) -> {
            // TODO respecter S & O
            /*Intent intent = new Intent(getContext(), ArtistActivity.class);
            Artist artist = track.getArtist();
            Gson gson = new Gson();
            intent.putExtra("artist", gson.toJson(artist));
            getContext().startActivity(intent);*/
        });

        return convertView;
    }
}
