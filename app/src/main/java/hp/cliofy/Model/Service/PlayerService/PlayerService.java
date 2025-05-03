package hp.cliofy.Model.Service.PlayerService;

import hp.cliofy.Model.Service.ApiClient;

public class PlayerService implements IPlayerService {
    private final String PATH = "https://api.spotify.com/v1/me/player/";

    @Override
    public void playWithOffset(String uri, int offset) {
        String endpoint = PATH + "play";
        String putData = "{\"context_uri\":\"" + uri + "\",\"offset\":{\"position\":" + offset + "}}";
        ApiClient.putRequest(endpoint, putData);
    }
}
