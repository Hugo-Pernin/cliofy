package hp.cliofy.Model.Service.PlaylistService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import hp.cliofy.Model.Item.Playlist;
import hp.cliofy.Model.Item.Track;

public interface IPlaylistService {
    CompletableFuture<List<Track>> getPlaylistTracks(Playlist playlist);
}
