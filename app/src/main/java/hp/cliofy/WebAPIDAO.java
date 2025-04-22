package hp.cliofy;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public void connect(Context context) {
        final String CODE_VERIFIER = generateRandomString(64);
        final byte[] HASHED = sha256(CODE_VERIFIER);
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
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
    }
}
