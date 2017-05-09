package zamataro.pulsossimetro2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LivePulsActivity extends AppCompatActivity {

   private static final int ATTIVA_BT_REQUEST = 1;
    private static final int MY_CONNECTION_REQUEST = 1;
    BluetoothAdapter myBluetoothAdapter = null;
    Button ConnectBtn;
    boolean CONNESSO = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_puls);

        //inizializza TestoLive
        TextView Testo_Live=(TextView) findViewById(R.id.A2LiveText);

        //inizializza ConnectBtn
        ConnectBtn = (Button) findViewById(R.id.ConnectBtnId);

        //Inizializza myBluetoothAdapter
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Se hai il bluetooth prova a connetterti
        if(myBluetoothAdapter==null){

            Toast.makeText(getApplicationContext(), "Non hai il Bluetooth", Toast.LENGTH_LONG).show();
        }
        else if (!myBluetoothAdapter.isEnabled()) {

            Intent enableBTintent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTintent, ATTIVA_BT_REQUEST );
        }

        //Setta ConnectBtn
        ConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CONNESSO){
                    //Disconnettiti
                }
                else{
                    //Connettiti
                    Intent apriLista = new Intent(LivePulsActivity.this, AndroidBluetooth.class);
                    startActivityForResult(apriLista, MY_CONNECTION_REQUEST);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            case ATTIVA_BT_REQUEST:
                if(resultCode== Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(),"Bluetooth Attivato!", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Bluetooth non attivato!", Toast.LENGTH_LONG).show();
                    //finish();
                }
                break;
        }
    }
}
