package hp.cliofy.Model.Service.PlayerService;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PlayerService implements IPlayerService {
    private final String PATH = "https://api.spotify.com/v1/me/player/";
    private final String accessToken;

    public PlayerService(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void playWithOffset(String uri, int offset) {
        Thread thread = new Thread(() -> {
            try {
                HttpURLConnection urlConnection = null;
                try {
                    String putData = "{\"context_uri\":\"" + uri + "\",\"offset\":{\"position\":" + offset + "}}";

                    URL url = new URL(PATH + "play");

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setChunkedStreamingMode(0);

                    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                    writer.write(putData);
                    writer.flush();
                    writer.close();
                    out.close();

                    int code = urlConnection.getResponseCode();
                    if (code !=  200) {
                        throw new IOException("Invalid response from server: " + code);
                    }

                    BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    JSONObject json = new JSONObject(rd.readLine());
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
    }
}
