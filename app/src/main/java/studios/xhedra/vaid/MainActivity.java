package studios.xhedra.vaid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;


@RequiresApi(api = Build.VERSION_CODES.DONUT)
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,View.OnClickListener{
    private String adhaarnumber = "";
    private TextView adhaar;
    private Button submit;
    private TextToSpeech engine;
    public ListView mList1;
    public FloatingActionButton speakButton1;

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    Button analyze,click;
    ImageView display;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, Context context)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.SENSOR_ORIENTATION);
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        int result;
        switch (rotationCompensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                Log.e("TEXT_RECOG", "Bad rotation value: " + rotationCompensation);
        }
        return result;
    }

    // Create file for camera dump
    String mCurrentPhotoPath;
    File photoFile = null;
    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "picture",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Fire up intent to take photo
    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("File Error in Camera", ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "studios.xhedra.vaid.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adhaar = (TextView) findViewById(R.id.addhar);
        submit = (Button)findViewById(R.id.submit);

        speakButton1 = (FloatingActionButton) findViewById(R.id.btn_speak1);
        speakButton1.setOnClickListener(this);

        voiceinputbuttons();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(),QueryPage.class);
                    startActivity(intent);

            }
        });

        analyze = findViewById(R.id.analyze);
        click = findViewById(R.id.click);
        display = findViewById(R.id.imageView2);
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display.setImageURI(Uri.parse(mCurrentPhotoPath));
                FirebaseVisionImage image = null;

                try {
                    image = FirebaseVisionImage.fromFilePath(getApplicationContext(), Uri.fromFile(photoFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();
                if (image != null) {
                    textRecognizer.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText result) {
                                   // Toast.makeText(getApplicationContext(), result.getText(), LENGTH_LONG).show();
                                    String resultText = result.getText();
                                    for(FirebaseVisionText.TextBlock bl : result.getTextBlocks()){
                                        for(FirebaseVisionText.Line line: bl.getLines()){
                                            if(line.getText().length() == 14){
                                                adhaarnumber = line.getText();
                                            }
                                        }
                                    }
                                    adhaar.setText(adhaarnumber);
                                    //Toast.makeText(MainActivity.this, adhaarnumber, Toast.LENGTH_SHORT).show();
                                    for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
                                        String blockText = block.getText();
                                        Float blockConfidence = block.getConfidence();
                                        List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                        Point[] blockCornerPoints = block.getCornerPoints();
                                        Rect blockFrame = block.getBoundingBox();
                                        for (FirebaseVisionText.Line line: block.getLines()) {
                                            String lineText = line.getText();
                                            Float lineConfidence = line.getConfidence();
                                            List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                            Point[] lineCornerPoints = line.getCornerPoints();
                                            Rect lineFrame = line.getBoundingBox();
                                            int counter = 0;

                                            for (FirebaseVisionText.Element element: line.getElements()) {
                                                String elementText = element.getText();
                                                counter++;
                                                if(counter == 7 || counter == 8 || counter == 9){
                                                    adhaarnumber += elementText;
                                                }
                                                Log.d("result", "onSuccess: " + elementText.toString());
                                                Float elementConfidence = element.getConfidence();
                                                List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                                Point[] elementCornerPoints = element.getCornerPoints();
                                                Rect elementFrame = element.getBoundingBox();
                                            }


                                        }
                                    }
                                    //checkAdhaar();
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), e.getMessage(), LENGTH_LONG).show();
                                        }
                                    });

                }else {
                    Toast.makeText(getApplicationContext(),"PHOTO NULL", LENGTH_LONG).show();
                }
            }
        });
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    public void checkAdhaar(){
        String adhaarCarry = "";
        for(int i = 0 ; i < adhaarnumber.length() ; i++){
            if(adhaarnumber.charAt(i) != ' '){
                adhaarCarry += adhaarnumber.charAt(i);
            }
        }
        if(adhaarCarry.compareTo(adhaar.getText().toString()) == 0){
            Toast.makeText(this, adhaarCarry +  " correct", Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();

        }
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
