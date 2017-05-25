package zamataro.arduinoandroidmionew;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class DiarioHistoricoActivity extends AppCompatActivity {

    public String key, title, message;
    public String TAG = "TAG";
    public String email;
    private ListView mList;
    private ArrayList<String> mArrayList = new ArrayList<>();
    private Firebase mRef;
    private FirebaseAuth auth;
    //public String menuItemName;
    //public String listItemName;
    //public int menuItemIndex;
    private AdapterView.OnItemClickListener mNotaClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            key = ((TextView) view).getText().toString();

            Intent i = new Intent(DiarioHistoricoActivity.this, NotaActivity.class);
            i.putExtra("EXTRA_KEY", key);
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diario_historico);
        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();
        mList = (ListView) findViewById(R.id.ListID);


        email = auth.getCurrentUser().getEmail();
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
                String key = (String) dataSnapshot.getKey();
                mArrayList.remove(key);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

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

        if (menuItemIndex == 0) {
            //Borrar

            key = listItemName;
            Firebase myRef = new Firebase("https://pulsossimetro-792dd.firebaseio.com/" + email + "/Note/" + key);
            myRef.removeValue();

        } else if (menuItemIndex == 1) {
            //Modificar
            key = listItemName;
            Intent i = new Intent(DiarioHistoricoActivity.this, DiarioModifyActivity.class);
            i.putExtra("EXTRA_KEY", key);
            startActivity(i);

        } else if (menuItemIndex == 2) {
            //Mostrar
            key = listItemName;
            Intent i = new Intent(DiarioHistoricoActivity.this, NotaActivity.class);
            i.putExtra("EXTRA_KEY", key);
            startActivity(i);

        }

        //TextView text = (TextView)findViewById(R.id.footer);
        //text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

}
