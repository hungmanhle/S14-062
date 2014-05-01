package com.vogerman.gateLocator;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView tvLongitude;
    private TextView tvLatitude;
    private TextView tvGateway;

    private double longitude, latitude;

    private WifiManager wifiMan;
    DhcpInfo dhcpInfo;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        tvLongitude = (TextView)findViewById(R.id.val_long);
        tvLatitude  = (TextView)findViewById(R.id.val_lat);
        tvGateway   = (TextView)findViewById(R.id.val_gate);

        wifiMan = (WifiManager)getSystemService(Context.WIFI_SERVICE);
    }

    public void onAction(View view) {
        dhcpInfo = wifiMan.getDhcpInfo();

        if(tvLongitude == null || tvLatitude == null || tvGateway == null)
        {
            tvLongitude = (TextView)findViewById(R.id.val_long);
            tvLatitude  = (TextView)findViewById(R.id.val_lat);
            tvGateway   = (TextView)findViewById(R.id.val_gate);
        }

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener()
        {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                 longitude = location.getLongitude();
                 latitude = location.getLatitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) { }

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        tvGateway.setText(formatIP(dhcpInfo.gateway));
        tvLatitude.setText(String.valueOf(latitude));
        tvLongitude.setText(String.valueOf(longitude));
    }



    /**
     * Wrapper for formatting a network address into a legible IPv4 address.
     * @param ipAddr address in network byte order
     * @return String of address as IPv4
     */
    public String formatIP(int ipAddr)
    {
        // Deprecated Function
        // formatIpAddress assumes IPv4
        // we also assume IPv
        return Formatter.formatIpAddress(ipAddr);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
