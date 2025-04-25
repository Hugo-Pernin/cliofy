package hp.cliofy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.protocol.types.Track;

public class MainActivity extends AppCompatActivity implements IObserver {
    private GeneralDAO generalDAO;
    private Button pauseResumeButton;
    private Button skipPreviousButton;
    private Button skipNextButton;
    private Switch shuffleSwitch;
    private TextView informations;
    private ImageView albumCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        skipPreviousButton = findViewById(R.id.skipPreviousButton);
        skipNextButton = findViewById(R.id.skipNextButton);
        shuffleSwitch = findViewById(R.id.shuffleSwitch);
        informations = findViewById(R.id.informations);
        albumCover = findViewById(R.id.albumCover);
        pauseResumeButton.setOnClickListener(this::pauseResumeClick);
        skipPreviousButton.setOnClickListener(this::skipPreviousClick);
        skipNextButton.setOnClickListener(this::skipNextClick);
        shuffleSwitch.setOnClickListener(this::shuffleClick);

        generalDAO = new GeneralDAO();
        generalDAO.addObserver(this);
        generalDAO.connect(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        generalDAO.removeObserver(this);
        generalDAO.disconnect();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith("com.hp.cliofy://callback")) {
            String authorizationCode = uri.getQueryParameter("code");
            if (authorizationCode != null) {
                Toast.makeText(this, "Connecté à l'API", Toast.LENGTH_SHORT).show();
                generalDAO.storeAuthorizationCode(authorizationCode);
            }
            else {
                Toast.makeText(this, "Erreur lors de la connexion à l'API : connexion refusée", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Erreur lors de la connexion à l'API : mauvaise redirection", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseResumeClick(View v) {
        generalDAO.pauseResume();
    }

    private void skipPreviousClick(View v) {
        generalDAO.skipPrevious();
    }

    private void skipNextClick(View v) {
        generalDAO.skipNext();
    }

    private void shuffleClick(View v) {
        if (shuffleSwitch.isChecked()) {
            generalDAO.enableShuffle();
        }
        else {
            generalDAO.disableShuffle();
        }
    }

    @Override
    public void pauseChange(boolean isPaused) {
        if (isPaused) {
            pauseResumeButton.setText("Resume");
        }
        else {
            pauseResumeButton.setText("Pause");
        }
    }

    @Override
    public void shuffleChange(boolean isShuffling) {
        shuffleSwitch.setChecked(isShuffling);
    }

    @Override
    public void trackChange(Track track) {
        informations.setText(
                track.name + "\n" +
                track.artist.name + "\n" +
                track.album.name
        );
    }

    @Override
    public void imageChange(Bitmap bitmap) {
        albumCover.setImageBitmap(bitmap);
    }
}