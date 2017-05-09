package zamataro.pulsossimetro2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button LiveButton=(Button) findViewById(R.id.Live);
        LiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLive=new Intent(getApplicationContext(), LivePulsActivity.class);
                startActivity(intentLive);
            }
        });
        Button StoricoButton=(Button) findViewById(R.id.Storico);
        StoricoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStorico=new Intent(getApplicationContext(), StoricoActivity.class);
                startActivity(intentStorico);
            }
        });
        Button DiarioButton=(Button) findViewById(R.id.Diario);
        DiarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDiario=new Intent(getApplicationContext(), DiarioActivity.class);
                startActivity(intentDiario);
            }
        });
    }
}
