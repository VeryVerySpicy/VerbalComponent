package com.example.verbalcomponentjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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

public class MainActivity extends AppCompatActivity {

    public static final Integer AUDIO_PERMISSION = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText editText;
    private ImageButton wandBtn;
    private TextView textView;
    private Handler handler;

    private String GAME_STATE_SAFE_0 = "The Way is Clear";
    private String GAME_STATE_SAFE_1 = "The slime was burnt to a crisp!";
    private String GAME_STATE_DANGER_0 = "A slime is approaching!";
    private String GAME_STATE_DANGER_1 = "You were eaten by a slime!";

    private String GAME_STATE_TUTORIAL_0 = "Oh no! There are slimes on the road back to the wizard's tower.";
    private String GAME_STATE_TUTORIAL_1 = "Hold down the wand icon to start chanting, release when you're done.";
    private String GAME_STATE_TUTORIAL_2 = "'Fireball' kills blue slimes, 'Icebolt' kills red slimes'";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isPermissionGranted()) {
            requestPermission();
        }

        editText = findViewById(R.id.edtSpeechText);
        wandBtn = findViewById(R.id.wandBtn);
        textView = findViewById(R.id.the_path);

        handler = new Handler();
        handler.postDelayed(runnable, 5000);

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
                textView.setText(GAME_STATE_SAFE_1);
                editText.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION);
        }
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
            if (currentString.equals(GAME_STATE_SAFE_0)) {
                currentString = GAME_STATE_DANGER_0;
            } else if (currentString.equals(GAME_STATE_SAFE_1)) {
                currentString = GAME_STATE_SAFE_0;
            } else if (currentString.equals(GAME_STATE_DANGER_0)) {
                currentString = GAME_STATE_DANGER_1;
            }
            final String tempString = currentString;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(tempString);
                }
            });
            //Change the textview again after 5 seconds
            handler.postDelayed(runnable, 5000);
        }
    };

}