package com.android.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
import android.provider.MediaStore;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.android.settings.R;
import com.android.settings.utils.ShortcutPickerHelper;

public class Lockscreens extends Activity {

    private ShortcutPickerHelper mPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new LockscreenPreferenceFragment()).commit();
    }

    public class LockscreenPreferenceFragment extends PreferenceFragment implements
            ShortcutPickerHelper.OnPickListener, OnPreferenceChangeListener {

        private static final String PREF_LOCKSCREEN_LAYOUT = "pref_lockscreen_layout";
        private static final String PREF_SMS_PICKER = "sms_picker";
	private static final String PREF_SMS_PICKER_1 = "sms_picker_1";
        private static final String PREF_SMS_PICKER_2 = "sms_picker_2";
        private static final String PREF_SMS_PICKER_3 = "sms_picker_3";
	private static final String PREF_USER_OVERRIDE = "lockscreen_user_timeout_override";

        ListPreference mLockscreenOption;

        Preference mSmsPicker;
	Preference mAppPicker1;
        Preference mAppPicker2;
        Preference mAppPicker3;

        private Preference mCurrentCustomActivityPreference;
        private String mCurrentCustomActivityString;
        private String mSmsIntentUri;
	private String mCustomAppUri1;
        private String mCustomAppUri2;
        private String mCustomAppUri3;
	CheckBoxPreference mLockScreenTimeoutUserOverride;

	private static final String TAG = "Lockscreens";
	public static final int REQUEST_PICK_WALLPAPER = 199;
    	private static final int REQUEST_PICK_SHORTCUT = 100;
    	public static final int SELECT_ACTIVITY = 2;
    	public static final int SELECT_WALLPAPER = 3;
    	private static final String WALLPAPER_NAME = "lockscreen_wallpaper.jpg";
    	Preference mLockscreenWallpaper;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.lockscreen_settings);

            mLockscreenOption = (ListPreference) findPreference(PREF_LOCKSCREEN_LAYOUT);
            mLockscreenOption.setOnPreferenceChangeListener(this);
            mLockscreenOption.setValue(Settings.System.getInt(
                    getActivity().getContentResolver(), Settings.System.LOCKSCREEN_LAYOUT,
                    0) + "");

	    mLockscreenWallpaper = findPreference("wallpaper");

            mSmsPicker = findPreference(PREF_SMS_PICKER);

	    mAppPicker1 = findPreference(PREF_SMS_PICKER_1);
            
            mAppPicker2 = findPreference(PREF_SMS_PICKER_2);
            
            mAppPicker3 = findPreference(PREF_SMS_PICKER_3);

            mPicker = new ShortcutPickerHelper(this.getActivity(), this);

            mSmsIntentUri = Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_SMS_INTENT);

	    mCustomAppUri1 = Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_APP_INTENT_1);

            mCustomAppUri2 = Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_APP_INTENT_2);

            mCustomAppUri3 = Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_APP_INTENT_3);

	mLockScreenTimeoutUserOverride = (CheckBoxPreference) findPreference(PREF_USER_OVERRIDE);

        mLockScreenTimeoutUserOverride.setChecked(Settings.Secure.getInt(getActivity()
                .getContentResolver(), Settings.Secure.LOCK_SCREEN_LOCK_USER_OVERRIDE,
                0) == 1);

	refreshSettings();
        setHasOptionsMenu(true);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                Preference preference) {
            if (preference == mSmsPicker) {
                mCurrentCustomActivityPreference = preference;
                mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_SMS_INTENT;
                mPicker.pickShortcut();
                return true;
	    } else if (preference == mAppPicker1) {
                mCurrentCustomActivityPreference = preference;
                mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_APP_INTENT_1;
                mPicker.pickShortcut();
                return true;    
            } else if (preference == mAppPicker2) {
                mCurrentCustomActivityPreference = preference;
                mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_APP_INTENT_2;
                mPicker.pickShortcut();
                return true;
            } else if (preference == mAppPicker3) {
                mCurrentCustomActivityPreference = preference;
                mCurrentCustomActivityString = Settings.System.LOCKSCREEN_CUSTOM_APP_INTENT_3;
                mPicker.pickShortcut();
                return true;  
	    } else if (preference == mLockscreenWallpaper) {

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
	    } else if (preference == mLockScreenTimeoutUserOverride) {
                Settings.Secure.putInt(getActivity().getContentResolver(),
                	Settings.Secure.LOCK_SCREEN_LOCK_USER_OVERRIDE,
                	((CheckBoxPreference) preference).isChecked() ? 1 : 0);
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

        public void refreshSettings() {
            mSmsPicker.setSummary(mPicker.getFriendlyNameForUri(mSmsIntentUri));

	    mAppPicker1.setSummary(mPicker.getFriendlyNameForUri(mCustomAppUri1));

            mAppPicker2.setSummary(mPicker.getFriendlyNameForUri(mCustomAppUri2));

            mAppPicker3.setSummary(mPicker.getFriendlyNameForUri(mCustomAppUri3));
        }

        @Override
        public void shortcutPicked(String uri, String friendlyName, boolean isApplication) {
            Log.e("ROMAN", "shortcut picked");
            if (Settings.System.putString(getContentResolver(), mCurrentCustomActivityString, uri)) {
                mCurrentCustomActivityPreference.setSummary(friendlyName);
            }
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference == mLockscreenOption) {
                int val = Integer.parseInt((String) newValue);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.LOCKSCREEN_LAYOUT, val);
               return true;
                
            }
            return false;
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

