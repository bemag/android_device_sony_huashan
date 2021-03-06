/*
 * Copyright (C) 2017 The LineageOS Project
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

package org.lineageos.settings.device;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

public class LightsEffectsPreferenceFragment extends PreferenceFragment {

    private static final String KEY_NOTIFICATION_SETTINGS = "notification_settings";
    private static final String KEY_LIGHTS_EFFECTS_MUSIC_ENABLE = "lights_effects_music_enable";
    private static final String KEY_LIGHTS_EFFECTS_MUSIC_GAIN = "lights_effects_music_gain";
    private static final String KEY_LIGHTS_EFFECTS_MUSIC_AWAKE = "lights_effects_music_awake";
    private static final String KEY_LIGHTS_EFFECTS_MUSIC_ALWAYS = "lights_effects_music_always";

    private Preference mNotificationSettings;
    private SwitchPreference mLightsEffectsMusicEnable;
    private ListPreference mLightsEffectsMusicGain;
    private SwitchPreference mLightsEffectsMusicAwake;
    private SwitchPreference mLightsEffectsMusicAlways;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.lights_effects_panel);

        mNotificationSettings = (Preference) findPreference(KEY_NOTIFICATION_SETTINGS);
        mNotificationSettings.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                intent.putExtra(SettingsUtils.EXTRA_SHOW_FRAGMENT_AS_SUBSETTING, true);
                startActivity(intent);
                return true;
            }
        });

        boolean lightsEffectsMusicEnable = getLightsEffectsMusicEnable();
        String lightsEffectsMusicGain = String.valueOf(getLightsEffectsMusicGain());
        boolean lightsEffectsMusicAwake = getLightsEffectsMusicAwake();
        boolean lightsEffectsMusicAlways = getLightsEffectsMusicAlways();

        mLightsEffectsMusicEnable = (SwitchPreference) findPreference(
                KEY_LIGHTS_EFFECTS_MUSIC_ENABLE);
        mLightsEffectsMusicEnable.setChecked(lightsEffectsMusicEnable);
        mLightsEffectsMusicEnable.setOnPreferenceChangeListener(mLightsEffectsMusicPrefListener);

        mLightsEffectsMusicGain = (ListPreference) findPreference(KEY_LIGHTS_EFFECTS_MUSIC_GAIN);
        mLightsEffectsMusicGain.setEnabled(lightsEffectsMusicEnable);
        mLightsEffectsMusicGain.setValueIndex(mLightsEffectsMusicGain.findIndexOfValue(
                lightsEffectsMusicGain));
        mLightsEffectsMusicGain.setOnPreferenceChangeListener(mLightsEffectsMusicPrefListener);

        mLightsEffectsMusicAwake = (SwitchPreference) findPreference(
                KEY_LIGHTS_EFFECTS_MUSIC_AWAKE);
        mLightsEffectsMusicAwake.setEnabled(lightsEffectsMusicEnable && !lightsEffectsMusicAlways);
        mLightsEffectsMusicAwake.setChecked(lightsEffectsMusicAwake || lightsEffectsMusicAlways);
        mLightsEffectsMusicAwake.setOnPreferenceChangeListener(mLightsEffectsMusicPrefListener);

        mLightsEffectsMusicAlways = (SwitchPreference) findPreference(
                KEY_LIGHTS_EFFECTS_MUSIC_ALWAYS);
        mLightsEffectsMusicAlways.setEnabled(lightsEffectsMusicEnable);
        mLightsEffectsMusicAlways.setChecked(lightsEffectsMusicAlways);
        mLightsEffectsMusicAlways.setOnPreferenceChangeListener(mLightsEffectsMusicPrefListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        getListView().setPadding(0, 0, 0, 0);
    }

    private void setLightsEffectsMusicEnable(boolean enable) {
        SettingsUtils.putInt(getContext(), SettingsUtils.LIGHTS_EFFECTS_MUSIC_ENABLE,
                enable ? 1 : 0);
    }

    private void setLightsEffectsMusicGain(int value) {
        SettingsUtils.putInt(getContext(), SettingsUtils.LIGHTS_EFFECTS_MUSIC_GAIN,
                value);
    }

    private void setLightsEffectsMusicAwake(boolean enable) {
        SettingsUtils.putInt(getContext(), SettingsUtils.LIGHTS_EFFECTS_MUSIC_AWAKE,
                enable ? 1 : 0);
    }

    private void setLightsEffectsMusicAlways(boolean enable) {
        SettingsUtils.putInt(getContext(), SettingsUtils.LIGHTS_EFFECTS_MUSIC_ALWAYS,
                enable ? 1 : 0);
    }

    private boolean getLightsEffectsMusicEnable() {
        return SettingsUtils.getInt(getContext(),
                SettingsUtils.LIGHTS_EFFECTS_MUSIC_ENABLE, 1) != 0;
    }

    private int getLightsEffectsMusicGain() {
        return SettingsUtils.getInt(getContext(),
                SettingsUtils.LIGHTS_EFFECTS_MUSIC_GAIN, 4);
    }

    private boolean getLightsEffectsMusicAwake() {
        return SettingsUtils.getInt(getContext(),
                SettingsUtils.LIGHTS_EFFECTS_MUSIC_AWAKE, 0) != 0;
    }

    private boolean getLightsEffectsMusicAlways() {
        return SettingsUtils.getInt(getContext(),
                SettingsUtils.LIGHTS_EFFECTS_MUSIC_ALWAYS, 0) != 0;
    }

    private Preference.OnPreferenceChangeListener mLightsEffectsMusicPrefListener =
        new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            final String key = preference.getKey();

            if (KEY_LIGHTS_EFFECTS_MUSIC_ENABLE.equals(key)) {
                final boolean enable = (boolean)value;
                setLightsEffectsMusicEnable(enable);
                mLightsEffectsMusicGain.setEnabled(enable);
                mLightsEffectsMusicAwake.setEnabled(enable && !getLightsEffectsMusicAlways());
                mLightsEffectsMusicAlways.setEnabled(enable);
            }
            else if (KEY_LIGHTS_EFFECTS_MUSIC_GAIN.equals(key)) {
                setLightsEffectsMusicGain(Integer.parseInt((String)value));
            }
            else if (KEY_LIGHTS_EFFECTS_MUSIC_AWAKE.equals(key)) {
                setLightsEffectsMusicAwake((boolean)value);
            }
            else if (KEY_LIGHTS_EFFECTS_MUSIC_ALWAYS.equals(key)) {
                final boolean enable = (boolean)value;
                setLightsEffectsMusicAlways(enable);
                mLightsEffectsMusicAwake.setEnabled(!enable);
                if (!getLightsEffectsMusicAwake()) {
                    setLightsEffectsMusicAwake(true);
                    mLightsEffectsMusicAwake.setChecked(true);
                }
            }

            return true;
        }
    };
}
