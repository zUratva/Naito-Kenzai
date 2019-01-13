package com.aizoban.naitokenzai;

import android.app.Application;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.aizoban.naitokenzai.models.Chapter;
import com.aizoban.naitokenzai.models.Manga;
import com.aizoban.naitokenzai.models.databases.FavouriteManga;
import com.aizoban.naitokenzai.models.databases.RecentChapter;
import com.aizoban.naitokenzai.models.downloads.DownloadChapter;
import com.aizoban.naitokenzai.models.downloads.DownloadManga;
import com.aizoban.naitokenzai.models.downloads.DownloadPage;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class NaitoKenzaiApplication extends Application {
    static {
        cupboard().register(Manga.class);
        cupboard().register(Chapter.class);
        cupboard().register(FavouriteManga.class);
        cupboard().register(RecentChapter.class);
        cupboard().register(DownloadManga.class);
        cupboard().register(DownloadChapter.class);
        cupboard().register(DownloadPage.class);

    }

    private static NaitoKenzaiApplication sInstance;

    public NaitoKenzaiApplication() {
        sInstance = this;
    }

    public static synchronized NaitoKenzaiApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initializePreferences();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Glide.get(this).clearMemory();

        Runtime.getRuntime().gc();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        Glide.get(this).trimMemory(level);
    }

    private void initializePreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
}
