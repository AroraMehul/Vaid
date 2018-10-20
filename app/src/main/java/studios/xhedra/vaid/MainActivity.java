package studios.xhedra.vaid;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText adhaar;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adhaar = (EditText)findViewById(R.id.addhar);
        submit = (Button)findViewById(R.id.submit);

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
}
