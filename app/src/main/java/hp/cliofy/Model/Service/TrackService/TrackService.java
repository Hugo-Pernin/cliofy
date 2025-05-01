package hp.cliofy.Model.Service.TrackService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Track;

public class TrackService implements ITrackService {
    private final String PATH = "https://api.spotify.com/v1/tracks/";
    private final String accessToken;

    public TrackService(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void hydrateTrack(Track track) {
        try {
            JSONObject json = getRequest(PATH + track.getId());
            track.setAlbum(new Album(
                    json.getJSONObject("album").getString("name"),
                    json.getJSONObject("album").getString("uri"),
                    json.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url")
            ));
            track.setArtist(new Artist(
                    json.getJSONArray("artists").getJSONObject(0).getString("name"),
                    json.getJSONArray("artists").getJSONObject(0).getString("uri"),
                    ""
                    //json.getJSONArray("artists").getJSONObject(0).getJSONArray("images").getJSONObject(0).getString("url")
            ));
            track.setDiscNumber(json.getInt("disc_number"));
            track.setDurationMs(json.getInt("duration_ms"));
            track.setTrackNumber(json.getInt("track_number"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
