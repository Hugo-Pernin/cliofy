package hp.cliofy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.spotify.protocol.types.Track;

public class MainActivity extends AppCompatActivity implements IObserver {
    private GeneralDAO generalDAO;
    private Button pauseResumeButton;
    private Button skipPreviousButton;
    private Button skipNextButton;
    private Switch shuffleSwitch;
    private TextView informations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        skipPreviousButton = findViewById(R.id.skipPreviousButton);
        skipNextButton = findViewById(R.id.skipNextButton);
        shuffleSwitch = findViewById(R.id.shuffleSwitch);
        informations = findViewById(R.id.informations);
        pauseResumeButton.setOnClickListener(this::pauseResumeClick);
        skipPreviousButton.setOnClickListener(this::skipPreviousClick);
        skipNextButton.setOnClickListener(this::skipNextClick);
        shuffleSwitch.setOnClickListener(this::shuffleClick);

        generalDAO = new GeneralDAO();
        generalDAO.addObserver(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        super.onStart();
        generalDAO.connect(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        generalDAO.disconnect();
    }

    private void pauseResumeClick(View v) {
        if (generalDAO.isPaused()) {
            generalDAO.resume();
        }
        else {
            generalDAO.pause();
        }
    }

    private void skipPreviousClick(View v) {
        generalDAO.skipPrevious();
    }

    private void skipNextClick(View v) {
        generalDAO.skipNext();
    }

    private void shuffleClick(View v) {
        if (generalDAO.isShuffling()) {
            generalDAO.disableShuffle();
        }
        else {
            generalDAO.enableShuffle();
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
}