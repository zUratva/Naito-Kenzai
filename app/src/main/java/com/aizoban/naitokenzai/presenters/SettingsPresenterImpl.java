/*
Last Updated : 13 Jan 2019

Changes:
    A. Removed google play store link from Settings menu
*/

package com.aizoban.naitokenzai.presenters;

import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.support.v4.app.FragmentActivity;

import com.aizoban.naitokenzai.R;
import com.aizoban.naitokenzai.controllers.NaitoKenzaiManager;
import com.aizoban.naitokenzai.controllers.QueryManager;
import com.aizoban.naitokenzai.views.SettingsView;
import com.aizoban.naitokenzai.views.fragments.DisclaimerFragment;
import com.aizoban.naitokenzai.views.fragments.OpenSourceLicensesFragment;

import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SettingsPresenterImpl implements SettingsPresenter {
    public static final String TAG = SettingsPresenterImpl.class.getSimpleName();

    private SettingsView mSettingsView;

    public SettingsPresenterImpl(SettingsView settingsView) {
        mSettingsView = settingsView;
    }

    @Override
    public void initializeViews() {
        mSettingsView.initializeToolbar();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // Commented on 13 Jan 2019 to removed google play-store link from "Settings" menu
        //if (preference.getKey().equals(mSettingsView.getContext().getString(R.string.preference_view_google_play_key))) {
        //    viewGooglePlayListing();
        //    return true;
        //} else
        if (preference.getKey().equals(mSettingsView.getContext().getString(R.string.preference_view_disclaimer_key))) {
            displayDisclaimer();
            return true;
        } else if (preference.getKey().equals(mSettingsView.getContext().getString(R.string.preference_clear_favourite_key))) {
            clearFavouriteMangaList();
            return true;
        } else if (preference.getKey().equals(mSettingsView.getContext().getString(R.string.preference_clear_recent_key))) {
            clearRecentChapterList();
            return true;
        } else if (preference.getKey().equals(mSettingsView.getContext().getString(R.string.preference_clear_image_cache_key))) {
            clearImageCache();
            return true;
        } else if (preference.getKey().equals(mSettingsView.getContext().getString(R.string.preference_view_open_source_licenses_key))) {
            viewOpenSourceLicenses();
            return true;
        }

        return false;
    }

    private void viewGooglePlayListing() {
        final String appPackageName = mSettingsView.getContext().getPackageName();

        try {
            mSettingsView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            mSettingsView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void displayDisclaimer() {
        if (((FragmentActivity)mSettingsView.getContext()).getSupportFragmentManager().findFragmentByTag(DisclaimerFragment.TAG) == null) {
            DisclaimerFragment disclaimerFragment = new DisclaimerFragment();

            disclaimerFragment.show(((FragmentActivity) mSettingsView.getContext()).getSupportFragmentManager(), DisclaimerFragment.TAG);
        }
    }

    private void clearFavouriteMangaList() {
        QueryManager
                .deleteAllFavouriteMangas()
                .onErrorReturn(new Func1<Throwable, Integer>() {
                    @Override
                    public Integer call(Throwable throwable) {
                        return 0;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .subscribe();

        mSettingsView.toastClearedFavourite();
    }

    private void clearRecentChapterList() {
        QueryManager
                .deleteAllRecentChapters()
                .onErrorReturn(new Func1<Throwable, Integer>() {
                    @Override
                    public Integer call(Throwable throwable) {
                        return 0;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .subscribe();

        mSettingsView.toastClearedRecent();
    }

    private void clearImageCache() {
        NaitoKenzaiManager
                .clearImageCache()
                .onErrorReturn(new Func1<Throwable, Boolean>() {
                    @Override
                    public Boolean call(Throwable throwable) {
                        return false;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .subscribe();

        mSettingsView.toastClearedImageCache();
    }

    private void viewOpenSourceLicenses() {
        if (((FragmentActivity)mSettingsView.getContext()).getSupportFragmentManager().findFragmentByTag(OpenSourceLicensesFragment.TAG) == null) {
            OpenSourceLicensesFragment openSourceLicensesFragment = new OpenSourceLicensesFragment();

            openSourceLicensesFragment.show(((FragmentActivity) mSettingsView.getContext()).getSupportFragmentManager(), OpenSourceLicensesFragment.TAG);
        }
    }
}
