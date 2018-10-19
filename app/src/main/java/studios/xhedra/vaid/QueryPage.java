package studios.xhedra.vaid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class QueryPage extends AppCompatActivity {

    private Button imageUpld,textUpld,audioUpld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_page);

        imageUpld = (Button)findViewById(R.id.upldImg);
        textUpld = (Button)findViewById(R.id.upldText);
        audioUpld = (Button)findViewById(R.id.upldAud);

        imageUpld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UploadImage.class);
                startActivity(intent);
            }
        });

        textUpld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UploadText.class);
                startActivity(intent);
            }
        });

        audioUpld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UploadAudio.class);
                startActivity(intent);
            }
        });
    }
}
