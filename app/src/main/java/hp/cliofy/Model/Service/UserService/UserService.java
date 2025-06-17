package hp.cliofy.Model.Service.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Playlist;
import hp.cliofy.Model.Service.ApiClient;

public class UserService implements IUserService {
    private final String PATH = "https://api.spotify.com/v1/me/";

    @Override
    public CompletableFuture<List<Playlist>> getPlaylistsList() {
        return CompletableFuture.supplyAsync(() -> {
            List<Playlist> result = new ArrayList<>();
            try {
                JSONObject json = ApiClient.getRequest(PATH + "playlists?limit=50").get();
                JSONArray array = json.getJSONArray("items");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String name = object.get("name").toString();
                    String uri = object.get("uri").toString();
                    String imageUrl = object.getJSONArray("images").getJSONObject(0).getString("url");
                    String owner = object.getJSONObject("owner").getString("display_name");
                    Playlist playlist = new Playlist(name, uri, imageUrl, owner);
                    result.add(playlist);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        });
    }

    @Override
    public CompletableFuture<List<Artist>> getTopArtists() {
        return CompletableFuture.supplyAsync(() -> {
            List<Artist> result = new ArrayList<>();
            try {
                JSONObject json = ApiClient.getRequest(PATH + "top/artists?time_range=short_term&limit=5").get();
                JSONArray array = json.getJSONArray("items");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String name = object.getString("name");
                    String uri = object.getString("uri");
                    String imageUrl = object.getJSONArray("images").getJSONObject(0).getString("url");
                    Artist artist = new Artist(name, uri, imageUrl);
                    result.add(artist);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        });
    }
}
