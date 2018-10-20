package studios.xhedra.vaid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class UploadImage extends AppCompatActivity {
    TextView takeImage;
    ImageView preview;
    Button nextBtn;
    Uri photoURI;
    static final int REQUEST_TAKE_PHOTO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        takeImage = findViewById(R.id.takeImage);
        nextBtn = findViewById(R.id.next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Chatbot.class);
                startActivity(intent);
            }
        });
        WindowManager manager = (WindowManager) getSystemService(Activity.WINDOW_SERVICE);
        int width, height;
        WindowManager.LayoutParams params;
        preview = findViewById(R.id.preview);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            width = manager.getDefaultDisplay().getWidth();
            height = manager.getDefaultDisplay().getHeight();
        } else {
            Point point = new Point();
            manager.getDefaultDisplay().getSize(point);
            width = point.x;
            height = point.y;
        }
        takeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = width;
        lp.height = height*4/5;
        this.getWindow().setAttributes(lp);

        this.setFinishOnTouchOutside(true);

    }
    // Create file for camera dump
    String mCurrentPhotoPath;
    File photoFile = null;
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
                photoURI = FileProvider.getUriForFile(this,
                        "studios.xhedra.vaid.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent takePictureIntent) {

        if (requestCode == REQUEST_TAKE_PHOTO) {
            if(resultCode == Activity.RESULT_OK){
                preview.setImageURI(photoURI);
                new SendImage().execute(mCurrentPhotoPath);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult



}

class SendImage extends AsyncTask<String, Void, String> {

    private Exception exception;

    protected String doInBackground(String... path) {
        try {

            File audioFile = new File(path[0]);
            final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            MultipartBody.Builder mMultipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            mMultipartBody.addFormDataPart("file", "image.jpg", RequestBody.create(MEDIA_TYPE_JPG, audioFile));


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
