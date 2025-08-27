package hp.cliofy.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;

import hp.cliofy.Model.Item.Playlist;
import hp.cliofy.Model.Service.FacadeService;
import hp.cliofy.R;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    public PlaylistAdapter(Context context, List<Playlist> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Playlist playlist = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_playlist, parent, false);
        }

        ImageView playlistCover = convertView.findViewById(R.id.playlistCover);
        TextView playlistName = convertView.findViewById(R.id.playlistName);
        Button playButton = convertView.findViewById(R.id.playButton);

        Glide.with(this.getContext()).load(playlist.getImageUrl()).into(playlistCover);
        playlistName.setText(playlist.getName());
        playButton.setOnClickListener((View view) -> FacadeService.getInstance(getContext()).play(playlist.getUri()));

        return convertView;
    }
}
