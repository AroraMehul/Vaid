package studios.xhedra.vaid;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class UploadAudio extends AppCompatActivity {
    FloatingActionButton record;
    private static String mFileName = null;
    private MediaRecorder mRecorder;
    private Boolean isRecording = false;
    private TextView statusText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_audio);
        record = findViewById(R.id.floatingActionButton);
        WindowManager manager = (WindowManager) getSystemService(Activity.WINDOW_SERVICE);
        int width, height;
        WindowManager.LayoutParams params;
        statusText = findViewById(R.id.StatusAudio);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            width = manager.getDefaultDisplay().getWidth();
            height = manager.getDefaultDisplay().getHeight();
        } else {
            Point point = new Point();
            manager.getDefaultDisplay().getSize(point);
            width = point.x;
            height = point.y;
        }
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/record.3gp";
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = width;
        lp.height = height*3/4;
        this.getWindow().setAttributes(lp);

        this.setFinishOnTouchOutside(true);
        record.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(!isRecording) {
                    requestRecordAudioPermission();
                    isRecording = true;
                }else{
                    stopRecording();
                    isRecording = false;
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestRecordAudioPermission() {

        String requiredPermission = Manifest.permission.RECORD_AUDIO;

        // If the user previously denied this permission then show a message explaining why
        // this permission is needed
        if (this.checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {
            startRecording();
        } else {

            Toast.makeText(this, "This app needs to record audio through the microphone....", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{requiredPermission}, 101);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRecording();
        }
    }
    private void startRecording(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("REC AUDIO", "prepare() failed");
        }

        mRecorder.start();
        statusText.setText("Listening");
    }




    private void stopRecording(){
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        statusText.setText("Tap mic to speak");
        new SendAudio().execute(mFileName);

        //uploadAudio();
    }
}

class SendAudio extends AsyncTask<String, Void, String> {

    private Exception exception;

    protected String doInBackground(String... path) {
        try {

            File audioFile = new File(path[0]);
            final MediaType MEDIA_TYPE_AUDIO = MediaType.parse("audio/3gp");
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            MultipartBody.Builder mMultipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            mMultipartBody.addFormDataPart("file", "audio.3gp", RequestBody.create(MEDIA_TYPE_AUDIO, audioFile));


            RequestBody mRequestBody = mMultipartBody.build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("http://172.20.53.23:8082/uploadfile").post(mRequestBody)
                    .build();

            okhttp3.Response response = client.newCall(request).execute();
            String responseString = response.body().string();
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
        return null;
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}