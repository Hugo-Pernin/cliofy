package hp.cliofy.Model.Service.UserService;

import java.util.List;

import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Playlist;

public interface IUserService {
    List<Playlist> getPlaylistsList();
    List<Artist> getTopArtists();
}
