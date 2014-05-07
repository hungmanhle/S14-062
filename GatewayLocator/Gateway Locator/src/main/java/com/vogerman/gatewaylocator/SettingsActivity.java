package com.vogerman.gatewaylocator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO Set ip and port defaults

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
    private static final String PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private EditTextPreference pref_ipAddr;
    private EditTextPreference pref_portNum;
    private CheckBoxPreference pref_saveLog;
    private ListPreference     pref_open;
    private ListPreference     pref_dellog;
    private SharedPreferences sharedPrefs;

    private Preference pref_wifi;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        pref_ipAddr = (EditTextPreference)findPreference("pref_ipaddr");
        pref_ipAddr.setSummary(sharedPrefs.getString("pref_ipaddr", ""));
        pref_ipAddr.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (o instanceof String && isIPv4((String) o)) {
                    return true;
                }
                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.tst_inval_ip), Toast.LENGTH_LONG).show();
                return false;
            }
        });

        pref_portNum = (EditTextPreference)findPreference("pref_portnum");
        pref_portNum.setSummary(sharedPrefs.getString("pref_portnum", ""));

        pref_saveLog = (CheckBoxPreference)findPreference("pref_savelog");
        pref_saveLog.setSummary(Environment.getExternalStorageDirectory().toString()
            + "/" + getResources().getString(R.string.app_name));
        pref_saveLog.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (o instanceof Boolean && ExternalStorage.isExternalStorageWritable()) {
                    return true;
                }
                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.tst_log_unavailable), Toast.LENGTH_LONG).show();
                return false;
            }
        });

        pref_open = (ListPreference)findPreference("pref_open");
        pref_dellog = (ListPreference)findPreference("pref_dellog");
        setLogProperties();

        pref_open.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object o) {
                try {
                    FileOpen.openFile(SettingsActivity.this, new File((String) o));
                } catch (IOException e) {
                } catch (IndexOutOfBoundsException e) {
                }

                return false;
            }
        });

        pref_dellog.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object o) {
                if(!(o instanceof String))
                    return false;
                final String opt = (String)o;
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                builder.setTitle(getResources().getString(R.string.dlg_del_title))
                        .setMessage(getResources().getString(R.string.dlg_del_body)
                                + "\n\n" + opt)
                        .setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new File(opt).delete();
                                setLogProperties();
                            }
                        })
                        .setNegativeButton(R.string.dlg_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setCancelable(true);
                AlertDialog dialog = builder.create();

                dialog.show();
                return false;
            }
        });

        pref_wifi = findPreference("pref_wifi");
        pref_wifi.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                return false;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String summaryStr;
        Preference pref = findPreference(key);
        if(pref instanceof EditTextPreference) {
            summaryStr = sharedPreferences.getString(key, "");
            pref.setSummary(summaryStr);
        }

        if(pref instanceof ListPreference) {
            CharSequence[] seq;

            seq = getLogEntries();
            pref_dellog.setEntries(seq);
            pref_open.setEntries(seq);

            seq = getLogValues();
            pref_dellog.setEntryValues(seq);
            pref_open.setEntryValues(seq);
        }
    }

    private void setLogProperties()
    {
        CharSequence[] values = getLogValues();
        if(values == null) {
            pref_open.setEnabled(false);
            pref_dellog.setEnabled(false);
        } else {
            CharSequence[] entries = getLogEntries();

            pref_dellog.setEntries(entries);
            pref_dellog.setEntryValues(values);

            pref_open.setEntries(entries);
            pref_open.setEntryValues(values);
        }
    }

    private CharSequence[] getLogEntries()
    {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + getResources().getString(R.string.app_name);

        final File folder = new File(rootPath);
        final File files[] = folder.listFiles();

        CharSequence[] entries = new CharSequence[files.length];
        if(files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                entries[i] = files[i].toString();
                entries[i] = entries[i].subSequence(rootPath.length() + 1, entries[i].length());
            }
        }
        return entries;
    }

    private CharSequence[] getLogValues()
    {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + getResources().getString(R.string.app_name);

        final File folder = new File(rootPath);
        final File files[] = folder.listFiles();

        CharSequence[] values = null;
        if(files != null && files.length > 0) {
            values = new CharSequence[files.length];
            for (int i = 0; i < files.length; i++) {
                values[i] = files[i].toString();
            }
        }
        return values;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public static boolean isIPv4(final String ip){

        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}