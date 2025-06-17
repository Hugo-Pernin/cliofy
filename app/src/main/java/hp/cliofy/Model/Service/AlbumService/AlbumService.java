package hp.cliofy.Model.Service.AlbumService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Track;
import hp.cliofy.Model.Service.ApiClient;

public class AlbumService implements IAlbumService {
    private final String PATH = "https://api.spotify.com/v1/albums/";

    @Override
    public CompletableFuture<List<Track>> getAlbumTracks(Album album) {
        return CompletableFuture.supplyAsync(() -> {
            List<Track> result = new ArrayList<>();
            try {
                JSONObject json = ApiClient.getRequest(PATH + album.getId() + "/tracks?limit=50").get();
                JSONArray array = json.getJSONArray("items");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String name = object.getString("name");
                    String uri = object.getString("uri");
                    Track track = new Track(name, uri);
                    result.add(track);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        });
    }
}
