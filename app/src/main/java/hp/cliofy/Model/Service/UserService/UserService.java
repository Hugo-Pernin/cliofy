package hp.cliofy.Model.Service.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Playlist;

public class UserService implements IUserService {
    private final String PATH = "https://api.spotify.com/v1/me/";
    private final String accessToken;

    public UserService(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public List<Playlist> getPlaylistsList() {
        List<Playlist> list = new ArrayList<>();

        try {
            JSONObject json = getRequest(PATH + "playlists?limit=50");
            JSONArray array = json.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.get("name").toString();
                String uri = object.get("uri").toString();
                String imageUrl = object.getJSONArray("images").getJSONObject(0).getString("url");
                Playlist playlist = new Playlist(name, uri, imageUrl);
                list.add(playlist);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<Artist> getTopArtists() {
        List<Artist> list = new ArrayList<>();

        try {
            JSONObject json = getRequest(PATH + "top/artists?time_range=short_term&limit=5");
            JSONArray array = json.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                String uri = object.getString("uri");
                String imageUrl = object.getJSONArray("images").getJSONObject(0).getString("url");
                Artist artist = new Artist(name, uri, imageUrl);
                list.add(artist);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private JSONObject getRequest(String endpoint) {
        final JSONObject[] json = {null}; // A one-entry array is necessary

        Thread thread = new Thread(() -> {
            try {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(endpoint);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                    urlConnection.setRequestMethod("GET");

                    int code = urlConnection.getResponseCode();
                    if (code !=  200) {
                        throw new IOException("Invalid response from server: " + code);
                    }

                    BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    json[0] = new JSONObject(rd.readLine());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return json[0];
    }
}
