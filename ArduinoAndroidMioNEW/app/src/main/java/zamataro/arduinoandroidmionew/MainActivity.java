package zamataro.arduinoandroidmionew;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    //public int FLAG = 1;
    private static final String TAG = "TAG";
    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String for MAC address
    public static String address;
    final int handlerState = 0;                         //used to identify handler message
    public double sensor0 = 0.00;
    public double sensor1 = 0.00;
    public double sensor2 = 0.00;
    public double sensor3 = 0.00;
    public List<Double> muestras = new ArrayList<>();
    public boolean flag = false;
    public double timeOld = System.currentTimeMillis();
    public double diffTime;
    public int index = 0;
    public double muestras_sum = 0;
    public double freq_cardiaca = 0;
    Button btnOn, btnOff;
    TextView txtString, txtStringLength, sensorView0, sensorView1, sensorView2, sensorView3;
    Handler bluetoothIn;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    //private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private ConnectedThread mConnectedThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Link the buttons and textViews to respective views
        btnOn = (Button) findViewById(R.id.buttonOn);
        btnOff = (Button) findViewById(R.id.buttonOff);
        txtString = (TextView) findViewById(R.id.txtString);
        txtStringLength = (TextView) findViewById(R.id.testView1);
        sensorView0 = (TextView) findViewById(R.id.SensorView0);
        sensorView1 = (TextView) findViewById(R.id.SensorView1);
        sensorView2 = (TextView) findViewById(R.id.SensorView2);
        sensorView3 = (TextView) findViewById(R.id.SensorView3);

        // we get graph view instance
        GraphView graph = (GraphView) findViewById(R.id.graph);
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(5);
        viewport.setMinX(0);
        viewport.setMaxX(1000);

        viewport.setScrollable(true);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                        //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                    //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        txtString.setText("Data Received = " + dataInPrint);
                        mConnectedThread.write("&");    // Send the message via bluetooth
                        int dataLength = dataInPrint.length();                            //get length of data received
                        txtStringLength.setText("String Length = " + String.valueOf(dataLength));
                        if (Float.parseFloat(String.valueOf(dataLength)) < 22) {
                            if (recDataString.charAt(0) == '#')                                //if it starts with # we know it is what we are looking for
                            {
                                String Sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                                String Sensor1 = recDataString.substring(6, 10);            //same again...
                                String Sensor2 = recDataString.substring(11, 15);
                                String Sensor3 = recDataString.substring(16, 20);
                                try {
                                    sensor0 = Float.parseFloat(Sensor0);
                                    sensor1 = Float.parseFloat(Sensor1);
                                    sensor2 = Float.parseFloat(Sensor2);
                                    sensor3 = Float.parseFloat(Sensor3);

                                    //detecto discesa
                                    if ((sensor0 < 3.5) && (flag == true)) {
                                        //Sto scendendo
                                        flag = false;
                                    }
                                    if ((sensor0 > 3.5) && (flag == false)) {
                                        //Sto salendo
                                        flag = true;
                                        double timeNew = System.currentTimeMillis();
                                        diffTime = timeNew - timeOld;
                                        //Log.d("TAG","diffTime "+diffTime);

                                        timeOld = timeNew;

                                        muestras.add(index, 60 * 1000 / diffTime);
                                        //Log.d("TAG",muestras.get(index).toString());

                                        index = index + 1;
                                        //ho un picco
                                        if (index > 15) {
                                            for (int i = 1; i < 10; i++) {
                                                muestras_sum = muestras_sum + muestras.get(index - i).doubleValue();
                                            }
                                            freq_cardiaca = muestras_sum / 10;
                                            //Log.d("TAG","sum "+muestras_sum);
                                            muestras_sum = 0;
                                            //Log.d("TAG","freq "+freq_cardiaca);
                                        }
                                    }

                                } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                sensorView0.setText(" Sensor 0 Voltage = " + Sensor0 + " V");    //update the textviews with sensor values
                                sensorView1.setText(" Sensor 1 Voltage = " + Sensor1 + " V");
                                sensorView2.setText(" Freq_Cardiaca = " + ((int) freq_cardiaca) + " BPM");//Se podria poner Sensor3 del arduino
                                sensorView3.setText(" Temp = " + "36.5" + " °C");// Se podria poner Sensor4 del Arduino

                            }
                        }


                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";

                    }
                }
            }
        };


        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra("EXTRA_DEVICE_ADDRESS");
        //address = intent.getExtras().toString();
        //Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address.toString());
        int a = 1;
        //BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
        btnOff.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("0");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Turn off LED", Toast.LENGTH_SHORT).show();
            }
        });

        btnOn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("1");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
            }
        });
        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");


    }//onResume

    //////////////////////////////////////////////////
    // add data to graph
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, sensor0), true, 1000);
    }

///////////////////////////////////////////////////

    @Override
    public void onPause() {
        super.onPause();
        try {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
                //Log.d("TAG","Sono prma del for");
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        addEntry();
                        //Log.d("TAG","HO AGGIUNTO UN ENTRATA");

                    }
                });

            }
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}

