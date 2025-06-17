package hp.cliofy.Model.Service.ArtistService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Track;
import hp.cliofy.Model.Service.ApiClient;

public class ArtistService implements IArtistService {
    private final String PATH = "https://api.spotify.com/v1/artists/";

    @Override
    public void hydrateArtist(Artist artist) {
        try {
            JSONObject json = ApiClient.getRequest(PATH + artist.getId()).get();
            artist.setFollowersTotal(json.getJSONObject("followers").getInt("total"));

            List<String> genres = new ArrayList<>();
            JSONArray genresArray = json.getJSONArray("genres");
            for (int i = 0; i < genresArray.length(); i++) {
                genres.add(genresArray.getString(i));
            }
            artist.setGenres(genres);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<List<Album>> getArtistAlbums(Artist artist, String type) {
        return CompletableFuture.supplyAsync(() -> {
            List<Album> result = new ArrayList<>();
            try {
                JSONObject json = ApiClient.getRequest(PATH + artist.getId() + "/albums?limit=50&include_groups=" + type).get();
                JSONArray array = json.getJSONArray("items");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String name = object.getString("name");
                    String uri = object.getString("uri");
                    String imageUrl = object.getJSONArray("images").getJSONObject(0).getString("url");
                    String albumType = object.getString("album_type");
                    int totalTracks = object.getInt("total_tracks");
                    String releaseDate = object.getString("release_date");
                    Album album = new Album(name, uri, imageUrl, albumType, totalTracks, releaseDate);
                    result.add(album);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        });
    }

    @Override
    public CompletableFuture<List<Track>> getArtistTopTracks(Artist artist) {
        return CompletableFuture.supplyAsync(() -> {
            List<Track> result = new ArrayList<>();
            try {
                JSONObject json = ApiClient.getRequest(PATH + artist.getId() + "/top-tracks").get();
                JSONArray array = json.getJSONArray("tracks");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String name = object.getString("name");
                    String uri = object.getString("uri");
                    String imageUrl = object.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                    Track track = new Track(name, uri, imageUrl);

                    JSONObject albumObject = object.getJSONObject("album");
                    String albumName = albumObject.getString("name");
                    String albumUri = albumObject.getString("uri");
                    String albumImageUrl = albumObject.getJSONArray("images").getJSONObject(0).getString("url");
                    String albumType = albumObject.getString("album_type");
                    int albumTotalTracks = albumObject.getInt("total_tracks");
                    String albumReleaseDate = albumObject.getString("release_date");
                    Album album = new Album(albumName, albumUri, albumImageUrl, albumType, albumTotalTracks, albumReleaseDate);
                    track.setAlbum(album);

                    result.add(track);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        });
    }
}
