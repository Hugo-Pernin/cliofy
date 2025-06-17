package hp.cliofy.Model.Service.AlbumService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Track;

// TODO commenter les interfaces
public interface IAlbumService {
    CompletableFuture<List<Track>> getAlbumTracks(Album album);
}
