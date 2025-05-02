package hp.cliofy.Model.Service.PlaylistService;

import java.util.List;

import hp.cliofy.Model.Item.Playlist;
import hp.cliofy.Model.Item.Track;

public interface IPlaylistService {
    List<Track> getPlaylistTracks(Playlist playlist);
}
