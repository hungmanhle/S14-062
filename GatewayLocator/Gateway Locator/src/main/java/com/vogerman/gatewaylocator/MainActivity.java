package com.vogerman.gatewaylocator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity implements LocationListener {

    private static final int VIB_LEN = 500;

    /* Handles to UI elements */
    private TextView tvLongitude;
    private TextView tvLatitude;
    private Button btnSend;
    private AlertDialog dialog;
    private Vibrator vib;

    /* Location variables */
    private double longitude, latitude;
    private LocationManager locationManager;

    /* Wifi Gateway containers */
    private WifiManager wifiMan;

    private SharedPreferences prefs;
    private Context context = this;

    private String ipAddr;
    private int port;

    public void onSend(View view)
    {
        btnSend.setVisibility(View.GONE);
        new Thread(new NetworkThread()).start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiMan = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        tvLongitude = (TextView)findViewById(R.id.val_long);
        tvLatitude  = (TextView)findViewById(R.id.val_lat);
        btnSend     = (Button)findViewById(R.id.btn_send);
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

        //TODO Check if a thread is not already running to display send button or not

        checkWifiSettings();
        checkLocationSettings();

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String ipAddr_dfeault = getResources().getString(R.string.pref_default_ip);
        ipAddr = prefs.getString("pref_ipaddr", ipAddr_dfeault);

        String port_default = getResources().getString(R.string.pref_default_port);
        port = (int)Integer.parseInt(prefs.getString("pref_portnum", port_default));

        List<String> enabledProviders;
        Criteria criteria = new Criteria();

        enabledProviders = locationManager.getProviders(criteria, true);
        if (enabledProviders.isEmpty()
                || !enabledProviders.contains(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(context, "Error...", Toast.LENGTH_LONG).show();
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

        tvLatitude.setText(String.valueOf(latitude));
        tvLongitude.setText(String.valueOf(longitude));
    }

    class NetworkThread implements Runnable {

        public void run() {
            String packet = latitude + " " + longitude;

            String filename = new SimpleDateFormat("yyyy-MM-dd")
                    .format(new Date()) + " Location Log.txt";

            TCPConnection connection;
            FileOutputStream fileStream;

            try {
                connection = TCPConnection.create(port, ipAddr);
            } catch (IOException e) {
                e.printStackTrace();

                showErrorToast(R.string.tst_connect_fail);
                showButton(btnSend);
                return;
            }

            try{
                connection.sendPacket(packet);
                showToast(connection.receive());
            } catch(IOException e) {
                showErrorToast(R.string.tst_send_fail);
            } finally {
                connection.close();
                showButton(btnSend);
            }

            if(prefs.getBoolean("pref_savelog", true)) {
                if(!ExternalStorage.isExternalStorageWritable()) {
                    showErrorToast(R.string.tst_ext_unavailable);
                    return;
                }
            } else {
                return;
            }

            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + getResources().getString(R.string.app_name);

            File folder = new File(rootPath);
            if(!folder.exists()) {
                folder.mkdir();
            }

            File file = new File(folder, filename);
            try {
                fileStream = new FileOutputStream(file, true);
                fileStream.write((packet + "\n").getBytes());
                fileStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                showErrorToast(R.string.tst_log_unavailable);
            } catch (IOException e) {
                e.printStackTrace();
                showErrorToast(R.string.tst_write_unavailable);

            }
            scanFile(file.getAbsolutePath());
        }
    }

    private void promptSettings(final int title, final int message, final String settings) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final Intent intent = new Intent(settings);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.dlg_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        locationManager.removeUpdates(this);
    }

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(context,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void showErrorToast(final int res) {
        showErrorToast(getResources().getString(res));
    }

    private void showErrorToast(final String toast) {
        if(prefs.getBoolean("pref_vib", true)) {
            vib.vibrate(VIB_LEN);
        }
        showToast(toast);
    }


    private void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run()
            { Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showButton(final Button btn) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id)
        {
            case R.id.action_settings:
                startActivity(new Intent(context, SettingsActivity.class));
                break;
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

    private void checkLocationSettings() {
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            promptSettings(R.string.dlg_gps_title,
                    R.string.dlg_gps_body,
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            return;
        }
    }

    private void checkWifiSettings() {
        if(!wifiMan.isWifiEnabled())
        {
            tvLatitude.setText(R.string.dlg_no_wifi);
            tvLongitude.setText(R.string.dlg_no_wifi);
            return;
        }
    }

}
