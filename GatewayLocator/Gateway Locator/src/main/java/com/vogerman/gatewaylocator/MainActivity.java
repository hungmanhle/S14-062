package com.vogerman.gatewaylocator;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


public class MainActivity extends Activity implements LocationListener {

    /* Handles to UI elements */
    private TextView tvLongitude;
    private TextView tvLatitude;
    private TextView tvGateway;

    private EditText etIPAddr;
    private EditText etPortNum;

    private double longitude, latitude;
    private LocationManager locationManager;

    /* Wfifi Gateway containers */
    private WifiManager wifiMan;
    DhcpInfo dhcpInfo;

    public void onAction(View view) {
        dhcpInfo = wifiMan.getDhcpInfo();
        tvGateway.setText(
                // Deprecated Function
                // formatIpAddress assumes IPv4
                // we also assume IPv4
                Formatter.formatIpAddress(dhcpInfo.gateway));
        tvLatitude.setText(String.valueOf(latitude));
        tvLongitude.setText(String.valueOf(longitude));
    }

    public void onSend(View view)
    {
        new Thread(new NetworkThread()).start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiMan = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        tvLongitude = (TextView)findViewById(R.id.val_long);
        tvLatitude  = (TextView)findViewById(R.id.val_lat);
        tvGateway   = (TextView)findViewById(R.id.val_gate);

        etIPAddr = (EditText)findViewById(R.id.fld_ipaddr);
        etPortNum = (EditText)findViewById(R.id.fld_port);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        tvLatitude.setText(String.valueOf(latitude));
        tvLongitude.setText(String.valueOf(longitude));
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> enabledProviders;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        enabledProviders = locationManager.getProviders(criteria, true);
        if (enabledProviders.isEmpty()
                || !enabledProviders.contains(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "Error...", Toast.LENGTH_LONG).show();
        } else {
            // Register every location provider returned from LocationManager
            for (String provider : enabledProviders) {
                // Register for updates every minute
                locationManager.requestLocationUpdates(provider,
                        60000L,  // minimum time of 60000 ms (1 minute)
                        0f,      // Minimum distance of 0
                        this);
            }
        }

        tvLatitude.setText("w---");
        tvLongitude.setText("w---");
    }

    class NetworkThread implements Runnable {

        public void run() {
            String packet = longitude + " " + latitude + " " + tvGateway.getText().toString();
            TCPConnection connection;
            try {
                // check ip address is valid
                connection = TCPConnection.create(Integer.parseInt(etPortNum.getText().toString()), etIPAddr.getText().toString());
            } catch(IOException e) {
                //Toast failed to connect
                return;
            }

            connection.appendToPacket(packet);

            try{
                connection.sendPacket();

            } catch(IOException e) {
                //Toast failed to send
            } finally {
                connection.close();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
