package studios.xhedra.vaid;

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

            }
        });

        textUpld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        audioUpld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
