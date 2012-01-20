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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class DelayAndTimeout extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String SLIDE_LOCK_DELAY_TOGGLE = "slide_lock_delay_toggle";

    private static final String SLIDE_LOCK_TIMEOUT_DELAY = "slide_lock_timeout_delay";

    private static final String SLIDE_LOCK_SCREENOFF_DELAY = "slide_lock_screenoff_delay";

    private LockPatternUtils mLockPatternUtils;

    private CheckBoxPreference mSlideLockDelayToggle;

    private ListPreference mSlideLockTimeoutDelay;

    private ListPreference mSlideLockScreenOffDelay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getPreferenceManager() != null) {

            addPreferencesFromResource(R.xml.delay_and_timeout);

            mLockPatternUtils = new LockPatternUtils(getActivity());

            PreferenceScreen prefSet = getPreferenceScreen();

            mSlideLockDelayToggle = (CheckBoxPreference) prefSet
                    .findPreference(SLIDE_LOCK_DELAY_TOGGLE);
            mSlideLockTimeoutDelay = (ListPreference) prefSet
                    .findPreference(SLIDE_LOCK_TIMEOUT_DELAY);
            mSlideLockScreenOffDelay = (ListPreference) prefSet
                    .findPreference(SLIDE_LOCK_SCREENOFF_DELAY);

            // Slide Lock
            mSlideLockDelayToggle.setChecked(Settings.System.getInt(getActivity()
                    .getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_LOCK_SLIDE_DELAY_TOGGLE, 0) == 1);

            int slideTimeoutDelay = Settings.System.getInt(getActivity().getApplicationContext()
                    .getContentResolver(),
                    Settings.System.SCREEN_LOCK_SLIDE_TIMEOUT_DELAY, 5000);
            mSlideLockTimeoutDelay.setValue(String.valueOf(slideTimeoutDelay));
            mSlideLockTimeoutDelay.setOnPreferenceChangeListener(this);

            int slideScreenOffDelay = Settings.System.getInt(getActivity().getApplicationContext()
                    .getContentResolver(),
                    Settings.System.SCREEN_LOCK_SLIDE_SCREENOFF_DELAY, 0);
            mSlideLockScreenOffDelay.setValue(String.valueOf(slideScreenOffDelay));
            mSlideLockScreenOffDelay.setOnPreferenceChangeListener(this);

            if (!mLockPatternUtils.isSecure()) {
                // hide slide lock preference category if lock screen set to NONE
                if (mLockPatternUtils.isLockScreenDisabled()) {
                    mSlideLockDelayToggle.setEnabled(false);
                }
            }
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mSlideLockDelayToggle) {
            value = mSlideLockDelayToggle.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_LOCK_SLIDE_DELAY_TOGGLE, value ? 1 : 0);
            return true;
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference == mSlideLockTimeoutDelay) {
            int slideTimeoutDelay = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_LOCK_SLIDE_TIMEOUT_DELAY,
                    slideTimeoutDelay);
            return true;
        } else if (preference == mSlideLockScreenOffDelay) {
            int slideScreenOffDelay = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_LOCK_SLIDE_SCREENOFF_DELAY, slideScreenOffDelay);
            return true;
        }

        return false;
    }

}
