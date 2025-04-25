package hp.cliofy;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONArray;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO communicating with the Spotify Web API
 */
public class WebAPIDAO {
    /**
     * Client ID found in the Spotify developer dashboard
     */
    private final String CLIENT_ID = "6837605e645041288ee6e45da7e46ff6";

    /**
     * Redirect URL entered in the Spotify developer dashboard
     */
    private final String REDIRECT_URI = "com.hp.cliofy://callback";

    /**
     * Permissions given to the DAO
     */
    private final String SCOPE = "user-read-private user-read-email playlist-read-private";

    /**
     * Code verifier used to get the access token
     */
    private String codeVerifier;

    /**
     * Authorization code used to get the access token
     */
    private String authorizationCode;

    /**
     * Access token used to make requests with the API
     */
    private String accessToken;

    /**
     * Refresh token used to get a new access token without asking user's authorization again (if the authorization token didn't expire)
     */
    private String refreshToken;

    /**
     * Connects the DAO to the Spotify Web API
     * @param context
     * TODO commenter
     */
    public void connect(Context context) {
        codeVerifier = generateRandomString(64);
        final byte[] HASHED = sha256(codeVerifier);
        final String CODE_CHALLENGE = base64encode(HASHED);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", CLIENT_ID);
        parameters.put("response_type", "code");
        parameters.put("redirect_uri", REDIRECT_URI);
        parameters.put("scope", SCOPE);
        parameters.put("code_challenge_method", "S256");
        parameters.put("code_challenge", CODE_CHALLENGE);

        StringBuilder url = new StringBuilder("https://accounts.spotify.com/authorize?");
        for (String key : parameters.keySet()) {
            url.append(key);
            url.append("=");
            url.append(parameters.get(key));
            url.append("&");
        }
        url.setLength(url.length() - 1); // Delete last '&'

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
        startActivity(context, browserIntent, null);
    }

    /**
     * Generates a random string
     * @param length desired length of the string
     * @return a string containing the desired number of random characters (can be uppercase letters, lowercase letters or digits)
     * TODO commenter
     */
    private String generateRandomString(int length) {
        final String POSSIBLE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(POSSIBLE_CHARS.length());
            result.append(POSSIBLE_CHARS.charAt(index));
        }

        return result.toString();
    }

    /**
     * Hashes a text using the SHA256 algorithm
     * @param text text to hash
     * @return hashed text using the SHA256 algorithm, converted to a table of bytes
     * TODO commenter
     */
    private byte[] sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encodes a table of bytes in base 64
     * @param input table of bytes to encode
     * @return encoded table of bytes in base 64, converted to a string
     * TODO commenter
     */
    private String base64encode(byte[] input) {
        String result = Base64.getEncoder().encodeToString(input);
        result = result.replace("=", "");
        result = result.replace("+", "-");
        result = result.replace("/", "_");
        return result;
    }

    /**
     * Stores the authorization code
     * @param authorizationCode authorization code to store
     */
    public void storeAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
        requestAccessToken(); // TODO why here?
    }

    /**
     * Requests the access token
     * TODO commenter
     */
    private void requestAccessToken() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HttpURLConnection urlConnection = null;
                    try {
                        String postData = "grant_type=authorization_code" +
                                "&code=" + authorizationCode +
                                "&redirect_uri=" + REDIRECT_URI +
                                "&client_id=" + CLIENT_ID +
                                "&code_verifier=" + codeVerifier;

                        URL url = new URL("https://accounts.spotify.com/api/token");

                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setDoOutput(true);
                        urlConnection.setDoInput(true);
                        urlConnection.setChunkedStreamingMode(0);

                        OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                        writer.write(postData);
                        writer.flush();

                        int code = urlConnection.getResponseCode();
                        if (code !=  200) {
                            throw new IOException("Invalid response from server: " + code);
                        }

                        BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        JSONObject json = new JSONObject(rd.readLine());
                        accessToken = json.get("access_token").toString();
                        refreshToken = json.get("refresh_token").toString();
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
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the playlists list of the current user
     * @return playlists list of the current user
     * TODO commenter
     */
    public List<Playlist> getPlaylistsList() {
        List<Playlist> list = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HttpURLConnection urlConnection = null;
                    try {
                        URL url = new URL("https://api.spotify.com/v1/me/playlists");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                        urlConnection.setRequestMethod("GET");

                        int code = urlConnection.getResponseCode();
                        if (code !=  200) {
                            throw new IOException("Invalid response from server: " + code);
                        }

                        BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line;
                        while ((line = rd.readLine()) != null) {
                            JSONObject json = new JSONObject(line);
                            JSONArray array = json.getJSONArray("items");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                String name = object.get("name").toString();
                                String uri = object.get("uri").toString();
                                Playlist playlist = new Playlist(name, uri);
                                list.add(playlist);
                            }
                        }
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
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return list;
    }
}
