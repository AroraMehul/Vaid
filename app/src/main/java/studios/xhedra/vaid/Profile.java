package studios.xhedra.vaid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    public ArrayList<String> pred_diseases = new ArrayList<>();
    public String est_resp_time ;
    public ArrayList<String> symptomes = new ArrayList<>();
    public TextView respTime;
    public TextView diseases;
    public TextView symptomesT;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        pred_diseases = getIntent().getStringArrayListExtra("Disease_Name");
        est_resp_time = getIntent().getStringExtra("Estimate_Time");
        symptomes = getIntent().getStringArrayListExtra("Symptomes");
        respTime = findViewById(R.id.respTime);
        diseases = findViewById(R.id.diseases);
        symptomesT = findViewById(R.id.symptomes);
        respTime.setText("Est. Response Time : " + est_resp_time);
        diseases.setText(pred_diseases.get(0));
        symptomesT.setText(symptomes.get(0));

    }

}
