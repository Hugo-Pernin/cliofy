package hp.cliofy.Model.Service.TrackService;

import org.json.JSONException;
import org.json.JSONObject;

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Track;
import hp.cliofy.Model.Service.ApiClient;

public class TrackService implements ITrackService {
    private final String PATH = "https://api.spotify.com/v1/tracks/";

    @Override
    public void hydrateTrack(Track track) {
        try {
            JSONObject json = ApiClient.getRequest(PATH + track.getId()).get();
            track.setAlbum(new Album(
                    json.getJSONObject("album").getString("name"),
                    json.getJSONObject("album").getString("uri"),
                    json.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url"),
                    json.getJSONObject("album").getString("album_type"),
                    json.getJSONObject("album").getInt("total_tracks"),
                    json.getJSONObject("album").getString("release_date")
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
