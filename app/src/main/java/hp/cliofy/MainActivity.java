package hp.cliofy;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private GeneralDAO generalDAO;
    private Button pauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(this::pause);

        generalDAO = new GeneralDAO();
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

    private void pause(View v) {
        generalDAO.pause();
    }
}