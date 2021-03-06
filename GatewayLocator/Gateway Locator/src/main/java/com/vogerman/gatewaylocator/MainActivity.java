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
import android.os.AsyncTask;
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

/**
 *
 */
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

    /**
     * On click action for the send button. It creates and excutes a new
     * NetworkTask to send the latitude and longitude to the server.
     * @param view
     */
    public void onSend(View view)
    {
        btnSend.setVisibility(View.GONE);
        new NetworkTask().execute(latitude + " " + longitude);
//        new Thread(new NetworkThread()).start();
    }

    /**
     * Initializes the UI and provides handles to the UI elements.
     * @param savedInstanceState
     */
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

    /**
     * Callback for when a change is noticed by the operating system.
     * This stores the new location and updates the UI elements.
     * @param location new location
     */
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        tvLatitude.setText(String.valueOf(latitude));
        tvLongitude.setText(String.valueOf(longitude));
    }

    /**
     * Checks if WiFi and Location Services are enabled, retreives the server's IP and port
     * from the preferences.
     *
     */
    @Override
    protected void onResume() {
        super.onResume();

        checkWifiSettings();
        checkLocationSettings();

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String ipAddr_dfeault = getResources().getString(R.string.pref_default_ip);
        ipAddr = prefs.getString("pref_ipaddr", ipAddr_dfeault);

        String port_default = getResources().getString(R.string.pref_default_port);
        port = (int)Integer.parseInt(prefs.getString("pref_portnum", port_default));


        /* Request location updates */
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

        /* Display location values */
        tvLatitude.setText(String.valueOf(latitude));
        tvLongitude.setText(String.valueOf(longitude));
    }

    /**
     * Encompasses all of the networking aspects. Connects, sends, and displays
     * feedback to the user and to a log file.
     */
    private class NetworkTask extends AsyncTask<String, String, String> {
        private TCPConnection connection;
        private boolean keepLog;

        private final String filename = new SimpleDateFormat("yyyy-MM-dd")
                        .format(new Date()) + " Location Log.txt";

        private String filePath;
        private File file;
        private FileOutputStream fileStream;

        /**
         * Sets up the log file.
         * Creates the file in the default extrnal storage folder under a folder
         * for our app's name.
         */
        @Override
        protected void onPreExecute() {
            if((keepLog = isLogging())) {
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + getResources().getString(R.string.app_name);
            }

            File folder = new File(filePath);
            if(!folder.exists()) {
                folder.mkdir();
            }

            file = new File(folder, filename);
        }

        /**
         * Bulk of the background task.
         * Sends a number of packets to the server. Encompasses major networking components
         * such as connecting and sending. Also writes to a log file.
         * @param packets strings that will be sent to the server
         * @return String will be shown as a toast to show feedback
         */
        @Override
        protected String doInBackground(String... packets) {

            SimpleDateFormat hourFormat = new SimpleDateFormat("H:mm:ss");
            String time = "";

            try {
                fileStream = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                time = hourFormat.format(new Date());
                connection = TCPConnection.create(port, ipAddr);
                publishProgress("Connection established!");
                fileStream.write((time + " Connection established" + "\n").getBytes());
            } catch (IOException e) {
                try {
                    fileStream.write((time + "FAILED!! connection at: " + "\n").getBytes());
                    fileStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
                vibrate();
                return getResources().getString(R.string.tst_connect_fail);
            }

            for (int i = 0; i < packets.length; i++) {
                try {
                    time = hourFormat.format(new Date());
                    fileStream.write(time.getBytes());
                    connection.sendPacket(packets[i]);
                    fileStream.write((" Sent packet[" + i + "]: " + packets[i] + "\n").getBytes());

                    time = hourFormat.format(new Date());
                    fileStream.write((time + " ").getBytes());
                    fileStream.write(("Received response: " + connection.receive() + "\n").getBytes());

                    publishProgress("Packet: " + (i + 1) + " sent");
                } catch (IOException e) {
                    time = hourFormat.format(new Date());
                    try {
                        fileStream.write((time + "FAILED!!! I/O" + "\n").getBytes());
                        connection.close();
                        fileStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                    vibrate();
                    publishProgress("Packet: " + (i + 1) + " failed");
                }             }

            try {
                fileStream.write("------------------------".getBytes());
                connection.close();
                fileStream.close();
            } catch (IOException e) {
                vibrate();
//                publishProgress("File close failed");
                e.printStackTrace();
            }

            return getResources().getString(R.string.tst_send_success);
        }

        /**
         * Displays feedback to the user.
         * @param strings the strings to show the feedback to the user
         */
        protected void onProgressUpdate(String... strings) {
            Toast.makeText(context, strings[0], Toast.LENGTH_SHORT).show();
        }

        /**
         * Clean up and exiting the NetworkTask.
         * @param string
         */
        protected void onPostExecute(String string) {
            showButton(btnSend);
            scanFile(file.getAbsolutePath());
            Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
        }

        /**
         * Helper for letting the system know a new file has been created.
         * @param path the location of the new file.
         */
        private void scanFile(String path) {
            MediaScannerConnection.scanFile(context,
                    new String[] { path }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("TAG", "Finished scanning " + path);
                        }
                    });
        }

        /**
         * Returns whether or not to keep a log file by checking if
         * the destination is writable and if the setting is enabled.
         * @return true if ready to write; false otherwise
         */
        private boolean isLogging() {
            if(prefs.getBoolean("pref_savelog", true)) {
                if(!ExternalStorage.isExternalStorageWritable()) {
                    showErrorToast(R.string.tst_ext_unavailable);
                    return false;
                }
            } else {
                return false;
            }
            return true;
        }

    }

    /**
     * Creates an AlertDialog that will launch an intent.
     * @param title AlertDialog title.
     * @param message AlertDialog message.
     * @param settings new Intent to start on AlertDialog positive action.
     */
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


    /**
     * Called when the activity is no longer visible.
     * Hides all AlertDialogs and removes location update requests.
     */
    @Override
    protected void onPause() {
        super.onPause();

        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        locationManager.removeUpdates(this);
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
        vibrate();
        showToast(toast);
    }


    private void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run()
            { Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
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

    /**
     * Checks if the user specified vibration settings and vibrates if necessary.
     */
    private void vibrate()
    {
        if(prefs.getBoolean("pref_vib", true)) {
            vib.vibrate(VIB_LEN);
        }
    }

    /**
     * Checks if location services are enabled. Prompts to change settings
     * if it isn't.
     */
    private void checkLocationSettings() {
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            promptSettings(R.string.dlg_gps_title,
                    R.string.dlg_gps_body,
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        }
    }

    /**
     * Checks if WiFi is enabled.
     */
    private void checkWifiSettings() {
        if(!wifiMan.isWifiEnabled())
        {
            tvLatitude.setText(R.string.dlg_no_wifi);
            tvLongitude.setText(R.string.dlg_no_wifi);
        }
    }

}
