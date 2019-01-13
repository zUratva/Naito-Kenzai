package com.aizoban.naitokenzai.presenters;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;

import com.aizoban.naitokenzai.BuildConfig;
import com.aizoban.naitokenzai.controllers.QueryManager;
import com.aizoban.naitokenzai.models.downloads.DownloadManga;
import com.aizoban.naitokenzai.presenters.mapper.DownloadMangaMapper;
import com.aizoban.naitokenzai.utils.SearchUtils;
import com.aizoban.naitokenzai.utils.wrappers.RequestWrapper;
import com.aizoban.naitokenzai.views.DownloadMangaView;
import com.aizoban.naitokenzai.views.activities.MangaActivity;
import com.aizoban.naitokenzai.views.adapters.DownloadMangaAdapter;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class DownloadMangaPresenterImpl implements DownloadMangaPresenter {
    public static final String TAG = DownloadMangaPresenterImpl.class.getSimpleName();

    private static final String SEARCH_NAME_PARCELABLE_KEY = TAG + ":" + "SearchNameParcelableKey";

    private static final String POSITION_PARCELABLE_KEY = TAG + ":" + "PositionParcelableKey";

    private DownloadMangaView mDownloadMangaView;
    private DownloadMangaMapper mDownloadMangaMapper;
    private DownloadMangaAdapter mDownloadMangaAdapter;

    private String mSearchName;

    private Parcelable mPositionSavedState;

    private Subscription mQueryDownloadMangaSubscription;
    private Subscription mSearchViewSubscription;
    private PublishSubject<Observable<String>> mSearchViewPublishSubject;

    public DownloadMangaPresenterImpl(DownloadMangaView downloadMangaView, DownloadMangaMapper downloadMangaMapper) {
        mDownloadMangaView = downloadMangaView;
        mDownloadMangaMapper = downloadMangaMapper;

        mSearchName = "";
    }

    @Override
    public void initializeViews() {
        mDownloadMangaView.initializeToolbar();
        mDownloadMangaView.initializeListView();
        mDownloadMangaView.initializeEmptyRelativeLayout();
    }

    @Override
    public void initializeSearch() {
        mSearchViewPublishSubject = PublishSubject.create();
        mSearchViewSubscription = Observable.switchOnNext(mSearchViewPublishSubject)
                .debounce(SearchUtils.TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        queryDownloadMangaFromDatabase();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(String query) {
                        if (query != null) {
                            mSearchName = query;
                        }

                        onCompleted();
                    }
                });
    }

    @Override
    public void initializeDataFromDatabase() {
        mDownloadMangaAdapter = new DownloadMangaAdapter(mDownloadMangaView.getContext());

        mDownloadMangaMapper.registerAdapter(mDownloadMangaAdapter);
    }

    @Override
    public void onResume() {
        queryDownloadMangaFromDatabase();
    }

    @Override
    public void saveState(Bundle outState) {
        if (mSearchName != null) {
            outState.putString(SEARCH_NAME_PARCELABLE_KEY, mSearchName);
        }
        if (mDownloadMangaMapper.getPositionState() != null) {
            outState.putParcelable(POSITION_PARCELABLE_KEY, mDownloadMangaMapper.getPositionState());
        }
    }

    @Override
    public void restoreState(Bundle savedState) {
        if (savedState.containsKey(SEARCH_NAME_PARCELABLE_KEY)) {
            mSearchName = savedState.getString(SEARCH_NAME_PARCELABLE_KEY);

            savedState.remove(SEARCH_NAME_PARCELABLE_KEY);
        }
        if (savedState.containsKey(POSITION_PARCELABLE_KEY)) {
            mPositionSavedState = savedState.getParcelable(POSITION_PARCELABLE_KEY);

            savedState.remove(POSITION_PARCELABLE_KEY);
        }
    }

    @Override
    public void destroyAllSubscriptions() {
        if (mQueryDownloadMangaSubscription != null) {
            mQueryDownloadMangaSubscription.unsubscribe();
            mQueryDownloadMangaSubscription = null;
        }
        if (mSearchViewSubscription != null) {
            mSearchViewSubscription.unsubscribe();
            mSearchViewSubscription = null;
        }
    }

    @Override
    public void releaseAllResources() {
        if (mDownloadMangaAdapter != null) {
            mDownloadMangaAdapter.setCursor(null);
            mDownloadMangaAdapter = null;
        }
    }

    @Override
    public void onDownloadMangaClick(int position) {
        if (mDownloadMangaAdapter != null) {
            DownloadManga selectedDownloadManga = (DownloadManga) mDownloadMangaAdapter.getItem(position);
            if (selectedDownloadManga != null) {
                String mangaSource = selectedDownloadManga.getSource();
                String mangaUrl = selectedDownloadManga.getUrl();

                Intent downloadMangaIntent = MangaActivity.constructOfflineMangaActivityIntent(mDownloadMangaView.getContext(), new RequestWrapper(mangaSource, mangaUrl));
                mDownloadMangaView.getContext().startActivity(downloadMangaIntent);
            }
        }    }

    @Override
    public void onQueryTextChange(String query) {
        if (mSearchViewPublishSubject != null) {
            mSearchViewPublishSubject.onNext(Observable.just(query));
        }
    }

    @Override
    public void onOptionToTop() {
        mDownloadMangaView.scrollToTop();
    }

    private void queryDownloadMangaFromDatabase() {
        if (mQueryDownloadMangaSubscription != null) {
            mQueryDownloadMangaSubscription.unsubscribe();
            mQueryDownloadMangaSubscription = null;
        }

        if (mSearchName != null) {
            mQueryDownloadMangaSubscription = QueryManager
                    .queryDownloadMangaFromName(mSearchName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Cursor>() {
                        @Override
                        public void onCompleted() {
                            restorePosition();
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNext(Cursor cursor) {
                            if (mDownloadMangaAdapter != null) {
                                mDownloadMangaAdapter.setCursor(cursor);
                            }

                            if (cursor != null && cursor.getCount() != 0) {
                                mDownloadMangaView.hideEmptyRelativeLayout();
                            } else {
                                mDownloadMangaView.showEmptyRelativeLayout();
                            }
                        }
                    });
        }
    }

    private void restorePosition() {
        if (mPositionSavedState != null) {
            mDownloadMangaMapper.setPositionState(mPositionSavedState);

            mPositionSavedState = null;
        }
    }
}
