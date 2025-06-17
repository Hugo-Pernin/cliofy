package hp.cliofy.Model.Service.UserService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Playlist;

public interface IUserService {
    CompletableFuture<List<Playlist>> getPlaylistsList();
    CompletableFuture<List<Artist>> getTopArtists();
}
