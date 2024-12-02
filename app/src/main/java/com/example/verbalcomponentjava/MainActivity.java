package com.example.verbalcomponentjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final Integer AUDIO_PERMISSION = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText editText;
    private ImageButton wandBtn;
    private TextView textView;
    private Handler handler;

    private int SLIMES_KILLED = 0;
    private int DELAY_TIME = 3000;

    private final String GAME_STATE_SAFE_0 = "The Way is Clear";
    private final String GAME_STATE_SAFE_1 = "The slime was slain!";
    private final String GAME_STATE_DANGER_0_0 = "A red slime is approaching!";
    private final String GAME_STATE_DANGER_0_1 = "A blue slime is approaching!";
    private final String GAME_STATE_DANGER_1 = "You were eaten by a slime!";

    private final String GAME_STATE_TUTORIAL_0 = "Oh no! There are slimes on the road back to the wizard's tower.";
    private final String GAME_STATE_TUTORIAL_1 = "Hold down the wand icon to start chanting, release when you're done.";
    private final String GAME_STATE_TUTORIAL_2 = "'Fire' kills blue slimes, 'Ice' kills red slimes'";
    private final String GAME_STATE_VICTORY = "You've made it back to your tower! Congradulations!";
    Random rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isPermissionGranted()) {
            requestPermission();
        }

        rand = new Random();

        editText = findViewById(R.id.edtSpeechText);
        wandBtn = findViewById(R.id.wandBtn);
        textView = findViewById(R.id.the_path);

        handler = new Handler();
        handler.postDelayed(runnable, DELAY_TIME);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
                editText.setText("");
                editText.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int i) {
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                assert data != null;
                if ((textView.getText().equals(GAME_STATE_DANGER_0_0) && data.get(0).equalsIgnoreCase("Ice"))
                        || (textView.getText().equals(GAME_STATE_DANGER_0_1) && data.get(0).equalsIgnoreCase("Fire"))) {
                    SLIMES_KILLED++;
                    textView.setText(GAME_STATE_SAFE_1);

                }
                editText.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });
        wandBtn.setImageResource(R.drawable.wand);
        wandBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                //ACTION_UP: A pressed gesture has finished.
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                    wandBtn.setImageResource(R.drawable.wand);
                }

                //ACTION_DOWN: A pressed gesture has started.
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    wandBtn.setImageResource(R.drawable.wand1);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION);
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AUDIO_PERMISSION && grantResults.length > 0) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String currentString = textView.getText().toString();
            if (SLIMES_KILLED >= 3) {
                textView.setText(GAME_STATE_VICTORY);
            } else {
                switch (currentString) {
                    case GAME_STATE_TUTORIAL_0:
                        currentString = GAME_STATE_TUTORIAL_1;
                        break;
                    case GAME_STATE_TUTORIAL_1:
                        currentString = GAME_STATE_TUTORIAL_2;
                        break;
                    case GAME_STATE_TUTORIAL_2:
                        currentString = GAME_STATE_SAFE_0;
                        DELAY_TIME = 5000;
                        break;
                    case GAME_STATE_SAFE_0:
                        if (rand.nextBoolean()) {
                            currentString = GAME_STATE_DANGER_0_0;
                        } else {
                            currentString = GAME_STATE_DANGER_0_1;
                        }
                        break;
                    case GAME_STATE_SAFE_1:
                        currentString = GAME_STATE_SAFE_0;
                        break;
                    case GAME_STATE_DANGER_0_0:
                    case GAME_STATE_DANGER_0_1:
                        currentString = GAME_STATE_DANGER_1;
                        break;
                }
                final String tempString = currentString;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(tempString);
                    }
                });
                //Change the textview again after 5 seconds
                handler.postDelayed(runnable, DELAY_TIME);
            }
        }
    };
}