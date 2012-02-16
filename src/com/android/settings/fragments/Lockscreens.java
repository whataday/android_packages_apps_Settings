package com.android.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.roman.romcontrol.utils.ShortcutPickerHelper;

public class Lockscreens extends SettingsPreferenceFragment implements
        ShortcutPickerHelper.OnPickListener, OnPreferenceChangeListener {

    private static final String TAG = "Lockscreens";

    public static final int REQUEST_PICK_WALLPAPER = 199;
    private static final int REQUEST_PICK_SHORTCUT = 100;
    public static final int SELECT_ACTIVITY = 2;
    public static final int SELECT_WALLPAPER = 3;

    private static final String WALLPAPER_NAME = "lockscreen_wallpaper.jpg";

    Preference mLockscreenWallpaper;

    private Preference mCurrentCustomActivityPreference;
    private String mCurrentCustomActivityString;

    private ShortcutPickerHelper mPicker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.lockscreen_settings);

        mLockscreenWallpaper = findPreference("wallpaper");

        mPicker = new ShortcutPickerHelper(this, this);

        
        refreshSettings();
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mLockscreenWallpaper) {

            int width = getActivity().getWallpaperDesiredMinimumWidth();
            int height = getActivity().getWallpaperDesiredMinimumHeight();
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            float spotlightX = (float) display.getWidth() / width;
            float spotlightY = (float) display.getHeight() / height;

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                    null);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", width);
            intent.putExtra("aspectY", height);
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);
            intent.putExtra("scale", true);
            // intent.putExtra("return-data", false);
            intent.putExtra("spotlightX", spotlightX);
            intent.putExtra("spotlightY", spotlightY);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getLockscreenExternalUri());
            intent.putExtra("outputFormat",
                    Bitmap.CompressFormat.JPEG.toString());

            startActivityForResult(intent, REQUEST_PICK_WALLPAPER);
            return true;
	}
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.lockscreens, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.remove_wallpaper:
                File f = new File(mContext.getFilesDir(), WALLPAPER_NAME);
                Log.e(TAG, mContext.deleteFile(WALLPAPER_NAME) + "");
                Log.e(TAG, mContext.deleteFile(WALLPAPER_NAME) + "");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private Uri getLockscreenExternalUri() {
        File dir = mContext.getExternalCacheDir();
        File wallpaper = new File(dir, WALLPAPER_NAME);

        return Uri.fromFile(wallpaper);
    }

    @Override
    public void shortcutPicked(String uri, String friendlyName, boolean isApplication) {
        if (Settings.System.putString(getActivity().getContentResolver(),
                mCurrentCustomActivityString, uri)) {
            mCurrentCustomActivityPreference.setSummary(friendlyName);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_WALLPAPER) {

                FileOutputStream wallpaperStream = null;
                try {
                    wallpaperStream = mContext.openFileOutput(WALLPAPER_NAME,
                            Context.MODE_PRIVATE);
                } catch (FileNotFoundException e) {
                    return; // NOOOOO
                }

                // should use intent.getData() here but it keeps returning null
                Uri selectedImageUri = getLockscreenExternalUri();
                Log.e(TAG, "Selected image uri: " + selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath());

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                        wallpaperStream);

            } else if (requestCode == REQUEST_PICK_SHORTCUT) {
                mPicker.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
