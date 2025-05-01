package hp.cliofy.Model.Service.AlbumService;

import java.util.List;

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Track;

// TODO commenter les interfaces
public interface IAlbumService {
    void hydrateAlbum(Album album);
    List<Track> getAlbumTracks(Album album);
}
