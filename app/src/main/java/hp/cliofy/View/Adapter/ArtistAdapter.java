package hp.cliofy.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;

import hp.cliofy.Model.Item.Artist;
import hp.cliofy.R;

public class ArtistAdapter extends ArrayAdapter<Artist> {
    public ArtistAdapter(Context context, List<Artist> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Artist artist = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_artist, parent, false);
        }

        ImageView artistImage = convertView.findViewById(R.id.artistImage);
        TextView artistName = convertView.findViewById(R.id.artistName);

        Glide.with(this.getContext()).load(artist.getImageUrl()).into(artistImage);
        artistName.setText(artist.getName());

        return convertView;
    }
}
