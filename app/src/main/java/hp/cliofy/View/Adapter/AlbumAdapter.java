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

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Service.FacadeService;
import hp.cliofy.R;

public class AlbumAdapter extends ArrayAdapter<Album> {
    public AlbumAdapter(Context context, List<Album> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Album album = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_album, parent, false);
        }

        ImageView albumCover = convertView.findViewById(R.id.albumCover);
        TextView albumName = convertView.findViewById(R.id.albumName);
        Button playButton = convertView.findViewById(R.id.playButton);

        Glide.with(this.getContext()).load(album.getImageUrl()).into(albumCover);
        albumName.setText(album.getName());
        playButton.setOnClickListener((View view) -> {
            FacadeService.getInstance().play(album.getUri());
        });

        return convertView;
    }
}
