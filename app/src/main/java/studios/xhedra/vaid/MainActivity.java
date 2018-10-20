package studios.xhedra.vaid;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,View.OnClickListener{

    private EditText adhaar;
    private Button submit;
    private TextToSpeech engine;
    public ListView mList1;
    public FloatingActionButton speakButton1;

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adhaar = (EditText)findViewById(R.id.addhar);
        submit = (Button)findViewById(R.id.submit);

        speakButton1 = (FloatingActionButton) findViewById(R.id.btn_speak1);
        speakButton1.setOnClickListener(this);

        voiceinputbuttons();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = adhaar.getText().toString();
                boolean result = Verhoeff.validateVerhoeff(num);
                String bool = String.valueOf(result);

                if(true){
                    Toast.makeText(MainActivity.this, "True", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),QueryPage.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            engine.setLanguage(Locale.getDefault());
        }
    }

    @Override
    public void onClick(View v) {
        startVoiceRecognitionActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speak(String s){
        engine.speak(s,TextToSpeech.QUEUE_FLUSH,null,null);
    }

    public void informationMenu() {
        startActivity(new Intent("android.intent.action.INFOSCREEN"));
    }

    public void voiceinputbuttons() {
        speakButton1 = (FloatingActionButton) findViewById(R.id.btn_speak1);
        mList1 = (ListView) findViewById(R.id.list1);
    }

    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mList1.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, matches));
            // matches is the result of voice input. It is a list of what the
            // user possibly said.
            // Using an if statement for the keyword you want to use allows the
            // use of any activity if keywords match
            // it is possible to set up multiple keywords to use the same
            // activity so more than one word will allow the user
            // to use the activity (makes it so the user doesn't have to
            // memorize words from a list)
            // to use an activity from the voice input information simply use
            // the following format;
            // if (matches.contains("keyword here") { startActivity(new
            // Intent("name.of.manifest.ACTIVITY")
            if(matches.contains("one") || matches.contains("Ek")){
                startActivity(new Intent(this,QueryPage.class));
            }

            if (matches.contains("information")) {
                informationMenu();
            }
        }

    }
}
