package com.aizoban.naitokenzai.presenters;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.aizoban.naitokenzai.BuildConfig;
import com.aizoban.naitokenzai.controllers.QueryManager;
import com.aizoban.naitokenzai.controllers.events.NavigationItemSelectEvent;
import com.aizoban.naitokenzai.models.Manga;
import com.aizoban.naitokenzai.utils.NavigationUtils;
import com.aizoban.naitokenzai.utils.wrappers.RequestWrapper;
import com.aizoban.naitokenzai.views.MainView;
import com.aizoban.naitokenzai.views.activities.MainActivity;
import com.aizoban.naitokenzai.views.activities.MangaActivity;
import com.aizoban.naitokenzai.views.fragments.CatalogueFragment;
import com.aizoban.naitokenzai.views.fragments.DownloadMangaFragment;
import com.aizoban.naitokenzai.views.fragments.FavouriteMangaFragment;
import com.aizoban.naitokenzai.views.fragments.LatestMangaFragment;
import com.aizoban.naitokenzai.views.fragments.NavigationFragment;
import com.aizoban.naitokenzai.views.fragments.QueueFragment;
import com.aizoban.naitokenzai.views.fragments.RecentChapterFragment;
import com.aizoban.naitokenzai.views.fragments.SettingsFragment;

import de.greenrobot.event.EventBus;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainPresenterImpl implements MainPresenter {
    public static final String TAG = MainPresenterImpl.class.getSimpleName();

    private static final String MAIN_FRAGMENT_PARCELABLE_KEY = TAG + ":" + "MainFragmentParcelableKey";
    private static final String PREFERENCE_FRAGMENT_PARCELABLE_KEY = TAG + ":" + "PreferenceFragmentParcelableKey";

    private MainView mMainView;

    private Fragment mFragment;
    private PreferenceFragment mPreferenceFragment;

    private int mInitialPosition;

    private Subscription mQueryRandomMangaSubscription;

    public MainPresenterImpl(MainView mainView) {
        mMainView = mainView;
    }

    @Override
    public void initializeViews() {
        mMainView.initializeToolbar();
        mMainView.initializeDrawerLayout();
    }

    @Override
    public void initializeMainLayout(Intent argument) {
        if (argument != null) {
            if (argument.hasExtra(MainActivity.POSITION_ARGUMENT_KEY)) {
                mInitialPosition = argument.getIntExtra(MainActivity.POSITION_ARGUMENT_KEY, NavigationUtils.POSITION_CATALOGUE);

                if (mInitialPosition == NavigationUtils.POSITION_CATALOGUE) {
                    mFragment = new CatalogueFragment();
                } else if (mInitialPosition == NavigationUtils.POSITION_LATEST) {
                    mFragment = new LatestMangaFragment();
                } else if (mInitialPosition == NavigationUtils.POSITION_DOWNLOAD) {
                    mFragment = new DownloadMangaFragment();
                } else if (mInitialPosition == NavigationUtils.POSITION_FAVOURITE) {
                    mFragment = new FavouriteMangaFragment();
                } else if (mInitialPosition == NavigationUtils.POSITION_RECENT) {
                    mFragment = new RecentChapterFragment();
                } else if (mInitialPosition == NavigationUtils.POSITION_QUEUE) {
                    mFragment = new QueueFragment();
                }

                argument.removeExtra(MainActivity.POSITION_ARGUMENT_KEY);
            }
        }

        if (mFragment == null) {
            mInitialPosition = NavigationUtils.POSITION_CATALOGUE;

            mFragment = new CatalogueFragment();
        }

        ((FragmentActivity)mMainView.getContext()).getSupportFragmentManager().beginTransaction()
                .add(mMainView.getMainLayoutId(), mFragment)
                .commit();
    }

    @Override
    public void initializeNavigationLayout() {
        Fragment navigationFragment = NavigationFragment.newInstance(mInitialPosition);

        ((FragmentActivity)mMainView.getContext()).getSupportFragmentManager().beginTransaction()
                .add(mMainView.getNavigationLayoutId(), navigationFragment)
                .commit();
    }

    @Override
    public void registerForEvents() {
        EventBus.getDefault().register(this);
    }

    public void onEventMainThread(NavigationItemSelectEvent event) {
        if (event != null) {
            mMainView.closeDrawerLayout();

            int position = event.getSelectedPosition();

            if (position == NavigationUtils.POSITION_CATALOGUE) {
                onPositionCatalogue();
            } else if (position == NavigationUtils.POSITION_LATEST){
                onPositionLatest();
            } else if (position == NavigationUtils.POSITION_EXPLORE){
                onPositionExplore();
            } else if (position == NavigationUtils.POSITION_DOWNLOAD){
                onPositionDownload();
            } else if (position == NavigationUtils.POSITION_FAVOURITE){
                onPositionFavourite();
            } else if (position == NavigationUtils.POSITION_RECENT){
                onPositionRecent();
            } else if (position == NavigationUtils.POSITION_QUEUE){
                onPositionQueue();
            } else if (position == NavigationUtils.POSITION_SETTINGS){
                onPositionSettings();
            }
        }
    }

    @Override
    public void unregisterForEvents() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void saveState(Bundle outState) {
        if (mFragment != null) {
            ((FragmentActivity) mMainView.getContext()).getSupportFragmentManager().putFragment(outState, MAIN_FRAGMENT_PARCELABLE_KEY, mFragment);
        }
        if (mPreferenceFragment != null) {
            ((FragmentActivity) mMainView.getContext()).getFragmentManager().putFragment(outState, PREFERENCE_FRAGMENT_PARCELABLE_KEY, mPreferenceFragment);
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(MAIN_FRAGMENT_PARCELABLE_KEY)) {
            mFragment = ((FragmentActivity) mMainView.getContext()).getSupportFragmentManager().getFragment(savedState, MAIN_FRAGMENT_PARCELABLE_KEY);

            savedState.remove(MAIN_FRAGMENT_PARCELABLE_KEY);
        }
        if (savedState.containsKey(PREFERENCE_FRAGMENT_PARCELABLE_KEY)) {
            mPreferenceFragment = (PreferenceFragment)((FragmentActivity) mMainView.getContext()).getFragmentManager().getFragment(savedState, PREFERENCE_FRAGMENT_PARCELABLE_KEY);

            savedState.remove(PREFERENCE_FRAGMENT_PARCELABLE_KEY);
        }
    }

    @Override
    public void destroyAllSubscriptions() {
        if (mQueryRandomMangaSubscription != null) {
            mQueryRandomMangaSubscription.unsubscribe();
            mQueryRandomMangaSubscription = null;
        }
    }

    private void onPositionCatalogue() {
        mFragment = new CatalogueFragment();

        removePreferenceFragment();
        replaceMainFragment();
    }

    private void onPositionLatest() {
        mFragment = new LatestMangaFragment();

        removePreferenceFragment();
        replaceMainFragment();
    }

    private void onPositionExplore() {
        if (mQueryRandomMangaSubscription != null) {
            mQueryRandomMangaSubscription.unsubscribe();
            mQueryRandomMangaSubscription = null;
        }

        mQueryRandomMangaSubscription = QueryManager
                .queryExploreMangaFromPreferenceSource()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Cursor>() {
                    @Override
                    public void onCompleted() {
                        // Do Nothing.
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(Cursor exploreCursor) {
                        if (exploreCursor != null && exploreCursor.getCount() != 0) {
                            Manga exploreManga = QueryManager.toObject(exploreCursor, Manga.class);
                            if (exploreManga != null) {
                                String mangaSource = exploreManga.getSource();
                                String mangaUrl = exploreManga.getUrl();

                                Intent mangaIntent = MangaActivity.constructOnlineMangaActivityIntent(mMainView.getContext(), new RequestWrapper(mangaSource, mangaUrl));
                                mMainView.getContext().startActivity(mangaIntent);
                            }
                        }
                    }
                });
    }

    private void onPositionDownload() {
        mFragment = new DownloadMangaFragment();

        removePreferenceFragment();
        replaceMainFragment();
    }

    private void onPositionFavourite() {
        mFragment = new FavouriteMangaFragment();

        removePreferenceFragment();
        replaceMainFragment();
    }

    private void onPositionRecent() {
        mFragment = new RecentChapterFragment();

        removePreferenceFragment();
        replaceMainFragment();
    }

    private void onPositionQueue() {
        mFragment = new QueueFragment();

        removePreferenceFragment();
        replaceMainFragment();
    }

    private void onPositionSettings() {
        mPreferenceFragment = new SettingsFragment();

        removeMainFragment();
        replacePreferenceFragment();
    }

    private void removePreferenceFragment() {
        if (mPreferenceFragment != null) {
            ((FragmentActivity) mMainView.getContext()).getFragmentManager().beginTransaction()
                    .remove(mPreferenceFragment)
                    .commit();

            mPreferenceFragment = null;
        }
    }

    private void replaceMainFragment() {
        ((FragmentActivity)mMainView.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(mMainView.getMainLayoutId(), mFragment)
                .commit();
    }

    private void removeMainFragment() {
        if (mFragment != null) {
            ((FragmentActivity) mMainView.getContext()).getSupportFragmentManager().beginTransaction()
                    .remove(mFragment)
                    .commit();

            mFragment = null;
        }
    }

    private void replacePreferenceFragment() {
        ((FragmentActivity)mMainView.getContext()).getFragmentManager().beginTransaction()
                .replace(mMainView.getMainLayoutId(), mPreferenceFragment)
                .commit();
    }
}
