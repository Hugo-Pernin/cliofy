package hp.cliofy.Model.Service.ArtistService;

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

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Track;

public class ArtistService implements IArtistService {
    private final String PATH = "https://api.spotify.com/v1/artists/";
    private final String accessToken;

    public ArtistService(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void hydrateArtist(Artist artist) {
        try {
            JSONObject json = getRequest(PATH + artist.getId());
            artist.setFollowersTotal(json.getJSONObject("followers").getInt("total"));

            List<String> genres = new ArrayList<>();
            JSONArray genresArray = json.getJSONArray("genres");
            for (int i = 0; i < genresArray.length(); i++) {
                genres.add(genresArray.getString(i));
            }
            artist.setGenres(genres);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Album> getArtistAlbums(Artist artist, String type) {
        List<Album> albums = new ArrayList<>();

        try {
            JSONObject json = getRequest(PATH + artist.getId() + "/albums?limit=50&include_groups=" + type);
            JSONArray array = json.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                String uri = object.getString("uri");
                String imageUrl = object.getJSONArray("images").getJSONObject(0).getString("url");
                Album album = new Album(name, uri, imageUrl);
                albums.add(album);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return albums;
    }

    @Override
    public List<Track> getArtistTopTracks(Artist artist) {
        List<Track> topTracks = new ArrayList<>();

        try {
            JSONObject json = getRequest(PATH + artist.getId() + "/top-tracks");
            JSONArray array = json.getJSONArray("tracks");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                String uri = object.getString("uri");
                String imageUrl = object.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                Track track = new Track(name, uri, imageUrl);
                topTracks.add(track);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return topTracks;
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
