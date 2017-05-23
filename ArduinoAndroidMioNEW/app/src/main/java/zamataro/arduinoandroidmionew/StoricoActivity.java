package zamataro.arduinoandroidmionew;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class StoricoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storico);
        TextView Testo_Live=(TextView) findViewById(R.id.A4StoricoText);
    }
}
