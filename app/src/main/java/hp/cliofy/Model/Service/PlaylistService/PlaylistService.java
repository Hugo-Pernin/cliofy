package hp.cliofy.Model.Service.PlaylistService;

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

import hp.cliofy.Model.Item.Playlist;
import hp.cliofy.Model.Item.Track;

public class PlaylistService implements  IPlaylistService {
    private final String PATH = "https://api.spotify.com/v1/playlists/";
    private final String accessToken;

    public PlaylistService(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void hydratePlaylist(Playlist playlist) {
        try {
            JSONObject json = getRequest(PATH + playlist.getId());
            playlist.setOwner(json.getJSONObject("owner").getString("display_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Track> getPlaylistTracks(Playlist playlist) {
        List<Track> tracks = new ArrayList<>();

        try {
            JSONObject json = getRequest(PATH + playlist.getId() + "/tracks?limit=50");
            JSONArray array = json.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i).getJSONObject("track");
                String name = object.getString("name");
                String uri = object.getString("uri");
                Track track = new Track(name, uri);
                tracks.add(track);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tracks;
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
