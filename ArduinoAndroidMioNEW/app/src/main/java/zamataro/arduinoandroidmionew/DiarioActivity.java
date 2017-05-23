package zamataro.arduinoandroidmionew;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;


import java.util.Calendar;

public class DiarioActivity extends AppCompatActivity {

    private Button mAddNoteBtn;
    private Button mViewNoteBtn;
    private Firebase mRef;
    private EditText mTitle;
    private EditText mMessage;
    private FirebaseAuth auth;
    public String TAG="TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diario);
        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();

        //NEWER VERSION
        //if(!FirebaseApp.getApps(this).isEmpty()) {
        //    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //}

        TextView Testo_Diario=(TextView) findViewById(R.id.A3DiarioText);
        mAddNoteBtn = (Button) findViewById(R.id.AddNoteBtn);
        mViewNoteBtn = (Button) findViewById(R.id.ViewNotesBtn);
        mMessage = (EditText) findViewById(R.id.EditMessage);
        mTitle = (EditText) findViewById(R.id.EditTitle);
        mViewNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiarioActivity.this, DiarioHistoricoActivity.class);
                startActivity(intent);
            }
        });
        mAddNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //See the time
                Calendar c = Calendar.getInstance();
                int seconds = c.get(Calendar.SECOND);
                int minute = c.get(Calendar.MINUTE);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int Data = c.get(Calendar.DATE);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                String date = Data + "-" + month + "-" + year + "_" + hour + ":" + minute + ":" + seconds;


                //Call the function to set the note
                callMref(date);
                //Set Strings
                String TitleValue = mTitle.getText().toString();
                String MessageValue = mMessage.getText().toString();

                //Set Title
                Firebase mRefTitle = mRef.child("Title");
                mRefTitle.setValue(TitleValue);

                //Set Message
                Firebase mRefMessage = mRef.child("Message");
                mRefMessage.setValue(MessageValue);

                Toast.makeText(getApplicationContext(), date + " " + getResources().getText(R.string.Add_Note_Toast).toString(), Toast.LENGTH_LONG).show();


            }
        });
    }

    private void callMref(String date){

        try {

            String email = auth.getCurrentUser().getEmail();
           // Log.d("TAG", email);
            email=email.replace('.','_');//i punti non sono caratteri validi per una subclasse
            mRef = new Firebase("https://pulsossimetro-792dd.firebaseio.com/"+email+"/Note/"+date);
           // Log.d("TAG", "SONO DOPO LA CHIAMATA mRef");
        }catch (Exception e){}


    }
}
