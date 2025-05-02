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

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Playlist;
import hp.cliofy.Model.Item.Track;
import hp.cliofy.Model.Service.ApiClient;

public class PlaylistService implements  IPlaylistService {
    private final String PATH = "https://api.spotify.com/v1/playlists/";
    private final String accessToken;

    public PlaylistService(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public List<Track> getPlaylistTracks(Playlist playlist) {
        List<Track> tracks = new ArrayList<>();

        try {
            JSONObject json = ApiClient.getRequest(PATH + playlist.getId() + "/tracks?limit=50");
            JSONArray array = json.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i).getJSONObject("track");
                String name = object.getString("name");
                String uri = object.getString("uri");
                Track track = new Track(name, uri);

                JSONObject albumObject = object.getJSONObject("album");
                String albumName = albumObject.getString("name");
                String albumUri = albumObject.getString("uri");
                String albumImageUrl = albumObject.getJSONArray("images").getJSONObject(0).getString("url");
                String albumType = albumObject.getString("album_type");
                int albumTotalTracks = albumObject.getInt("total_tracks");
                String albumReleaseDate = albumObject.getString("release_date");
                Album album = new Album(albumName, albumUri, albumImageUrl, albumType, albumTotalTracks, albumReleaseDate);
                track.setAlbum(album);

                tracks.add(track);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tracks;
    }
}
