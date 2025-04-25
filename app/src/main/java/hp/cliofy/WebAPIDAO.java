package hp.cliofy;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class WebAPIDAO {
    private final String CLIENT_ID = "6837605e645041288ee6e45da7e46ff6";
    private final String REDIRECT_URI = "com.hp.cliofy://callback";
    private final String SCOPE = "user-read-private user-read-email";
    private String codeVerifier;
    private String authorizationCode;
    private String accessToken;
    private String refreshToken;

    public void connect(Context context) {
        codeVerifier = generateRandomString(64);
        final byte[] HASHED = sha256(codeVerifier);
        final String CODE_CHALLENGE = base64encode(HASHED);

        HttpURLConnection httpURLConnection = null;
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("client_id", CLIENT_ID);
        parameters.put("response_type", "code");
        parameters.put("redirect_uri", REDIRECT_URI);
        parameters.put("scope", SCOPE);
        parameters.put("code_challenge_method", "S256");
        parameters.put("code_challenge", CODE_CHALLENGE);

        String url = "https://accounts.spotify.com/authorize?";
        for (String key : parameters.keySet()) {
            url += key + "=" + parameters.get(key) + "&";
        }
        url = url.substring(0, url.length() - 1); // Delete last '&'

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(context, browserIntent, null);
    }

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

    private byte[] sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String base64encode(byte[] input) {
        String result = Base64.getEncoder().encodeToString(input);
        result = result.replace("=", "");
        result = result.replace("+", "-");
        result = result.replace("/", "_");
        return result;
    }

    public void storeAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
        requestAccessToken();
    }

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
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
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
                        //getPlaylistsList();
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
    }

    /*public void getPlaylistsList() {
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
            JSONObject json = new JSONObject(rd.readLine());
            String s = "";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }*/
}
