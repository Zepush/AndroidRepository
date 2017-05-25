package zamataro.arduinoandroidmionew;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;


public class NotaActivity extends AppCompatActivity {

    public String key, title, message;
    public String TAG = "TAG";
    private Firebase mRef;
    private TextView mTitle;
    private TextView mMessage;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota);
        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();
        mTitle = (TextView) findViewById(R.id.Title);
        mMessage = (TextView) findViewById(R.id.Message);

        Intent intent = getIntent();

        key = intent.getStringExtra("EXTRA_KEY");

        String email = auth.getCurrentUser().getEmail();
        email=email.replace('.','_');//i punti non sono caratteri validi per una subclasse

        mRef = new  Firebase("https://pulsossimetro-792dd.firebaseio.com/" + email +"/Note/"+ key);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                title = map.get("Title");
                message = map.get("Message");
                mTitle.setText(title);
                mMessage.setText(message);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }


}
