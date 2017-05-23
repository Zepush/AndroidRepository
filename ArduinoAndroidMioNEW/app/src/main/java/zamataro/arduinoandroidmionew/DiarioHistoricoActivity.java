package zamataro.arduinoandroidmionew;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Map;

public class DiarioHistoricoActivity extends AppCompatActivity {

    private ListView mList;
    private ArrayList<String> mArrayList = new ArrayList<>();
    private Firebase mRef;
    public String key,title,message;
    public String TAG="TAG";
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diario_historico);
        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();
        mList = (ListView) findViewById(R.id.ListID);


        String email = auth.getCurrentUser().getEmail();
        email=email.replace('.','_');//i punti non sono caratteri validi per una subclasse, li sostituisco con un underscore

        mRef = new Firebase("https://pulsossimetro-792dd.firebaseio.com/" + email +"/Note");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrayList);
        mList.setAdapter(arrayAdapter);
        registerForContextMenu(mList);

        mList.setOnItemClickListener(mNotaClickListener);

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String key = (String) dataSnapshot.getKey();
                mArrayList.add(key);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
    private AdapterView.OnItemClickListener mNotaClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            key = ((TextView) view).getText().toString();

            // Make an intent to start next activity while taking an extra which is the MAC address.
            Intent i = new Intent(DiarioHistoricoActivity.this, NotaActivity.class);
            i.putExtra("EXTRA_KEY", key);
            startActivity(i);
        }
    };

    public void onCreateContextMenu(final ContextMenu menu,
                                    final View v, final ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId()==R.id.ListID) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(mArrayList.get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(menu.NONE , i, i, menuItems[i]);
            }
        }

    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = mArrayList.get(info.position);

        //TextView text = (TextView)findViewById(R.id.footer);
        //text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

}
