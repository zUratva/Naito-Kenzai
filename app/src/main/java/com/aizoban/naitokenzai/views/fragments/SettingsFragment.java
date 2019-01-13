/*
Last Updated : 13 Jan 2019

Changes: Sdkversion 28 compatibility updates
    1. Updated deprecated class "ActionBarActivity"
        to "AppCompatActivity"
*/

package com.aizoban.naitokenzai.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.aizoban.naitokenzai.R;
import com.aizoban.naitokenzai.presenters.SettingsPresenter;
import com.aizoban.naitokenzai.presenters.SettingsPresenterImpl;
import com.aizoban.naitokenzai.views.SettingsView;

public class SettingsFragment extends PreferenceFragment implements SettingsView, Preference.OnPreferenceClickListener {
    public static final String TAG = SettingsFragment.class.getSimpleName();

    private SettingsPresenter mSettingsPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettingsPresenter = new SettingsPresenterImpl(this);

        addPreferencesFromResource(R.xml.preferences);

        findPreference(getString(R.string.preference_view_google_play_key)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.preference_view_disclaimer_key)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.preference_clear_favourite_key)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.preference_clear_recent_key)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.preference_clear_image_cache_key)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.preference_view_open_source_licenses_key)).setOnPreferenceClickListener(this);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSettingsPresenter.initializeViews();
    }

    // SettingsView:

    @Override
    public void initializeToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.fragment_settings);
        }
    }

    @Override
    public void toastClearedFavourite() {
        Toast.makeText(getActivity(), R.string.toast_cleared_favourite, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastClearedRecent() {
        Toast.makeText(getActivity(), R.string.toast_cleared_recent, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastClearedImageCache() {
        Toast.makeText(getActivity(), R.string.toast_cleared_image_cache, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    // Preference.OnPreferenceClickListener:

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return mSettingsPresenter.onPreferenceClick(preference);
    }
}
