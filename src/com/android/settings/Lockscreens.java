package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

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
	    } else if (preference == mLockScreenTimeoutUserOverride) {
                Settings.Secure.putInt(getActivity().getContentResolver(),
                	Settings.Secure.LOCK_SCREEN_LOCK_USER_OVERRIDE,
                	((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;
            }

            return super.onPreferenceTreeClick(preferenceScreen, preference);
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

    

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("ROMAN", "on activity result");
        mPicker.onActivityResult(requestCode, resultCode, data);
    }

}

