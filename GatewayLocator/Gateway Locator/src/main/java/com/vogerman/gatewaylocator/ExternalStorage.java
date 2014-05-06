package com.vogerman.gatewaylocator;

import android.os.Environment;

/**
 * DO NOT FORGET PERMISSION - Write permission implies read also
 *     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * Created by German on 5/4/2014.
 */
public class ExternalStorage {

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
