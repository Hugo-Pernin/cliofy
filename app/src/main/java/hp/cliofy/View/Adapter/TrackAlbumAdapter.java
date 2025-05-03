package hp.cliofy.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import hp.cliofy.Model.Item.Track;
import hp.cliofy.Model.Service.FacadeService;
import hp.cliofy.R;

public class TrackAlbumAdapter extends ArrayAdapter<Track> {
    public TrackAlbumAdapter(Context context, List<Track> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Track track = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_track_album, parent, false);
        }

        TextView trackName = convertView.findViewById(R.id.trackName);
        Button queueButton = convertView.findViewById(R.id.queueButton);

        trackName.setText(track.getName());
        queueButton.setOnClickListener((View view) -> {
            FacadeService.getInstance().addItemToPlaybackQueue(track.getUri());
        });

        return convertView;
    }
}
