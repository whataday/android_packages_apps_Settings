/*
 * Copyright (C) 2012 CyanogenMod
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class UnlockOptions extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "UnlockOptions";

    private static final String MENU_UNLOCK_PREF = "menu_unlock";

    private static final String LOCKSCREEN_QUICK_UNLOCK_CONTROL = "quick_unlock_control";

    private CheckBoxPreference mMenuUnlock;

    private CheckBoxPreference mQuickUnlockScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getPreferenceManager() != null) {
            addPreferencesFromResource(R.xml.unlock_options);

            PreferenceScreen prefSet = getPreferenceScreen();

            /* Quick Unlock Screen Control */
            mQuickUnlockScreen = (CheckBoxPreference) prefSet
                    .findPreference(LOCKSCREEN_QUICK_UNLOCK_CONTROL);
            mQuickUnlockScreen.setChecked(Settings.System.getInt(getActivity()
                    .getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, 0) == 1);

            /* Menu Unlock */
            mMenuUnlock = (CheckBoxPreference) prefSet.findPreference(MENU_UNLOCK_PREF);
            mMenuUnlock.setChecked(Settings.System.getInt(getActivity().getApplicationContext()
                    .getContentResolver(),
                    Settings.System.MENU_UNLOCK_SCREEN, 0) == 1);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mQuickUnlockScreen) {
            value = mQuickUnlockScreen.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, value ? 1 : 0);
            return true;
        } else if (preference == mMenuUnlock) {
            value = mMenuUnlock.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.MENU_UNLOCK_SCREEN, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

}
