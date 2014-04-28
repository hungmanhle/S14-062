package com.example.ipfinder;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {
    DhcpInfo d;
    WifiManager wm;
    private TextView latitudeField;
    private TextView longitudeField;

    private TextView gatewayFeild;
    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gatewayFeild = ((TextView)findViewById(R.id.console));
        latitudeField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);

        final Button button = (Button) findViewById(R.id.dostuff);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                d = wm.getDhcpInfo();



                gatewayFeild.setText(formatIP(d.gateway));



                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                Criteria criteria = new Criteria();
                provider = locationManager.getBestProvider(criteria, false);
                Location location = locationManager.getLastKnownLocation(provider);

                //init
                if(location != null){
                    System.out.println("Provider: " + provider + " has been selected.");
                    onLocationChanged(location);
                } else {
                    latitudeField.setText("Latitude unavailable");
                    longitudeField.setText("Longitude unavailable");
                }
            }
        });


    }
    public String formatIP(int ipint)
    {

        return Formatter.formatIpAddress(ipint);
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


    public void onLocationChanged(Location location){
        float latitude = (float) (location.getLatitude());
        float longitude = (float) (location.getLongitude());
        latitudeField.setText(String.valueOf(latitude));
        longitudeField.setText(String.valueOf(longitude));

    }

}
