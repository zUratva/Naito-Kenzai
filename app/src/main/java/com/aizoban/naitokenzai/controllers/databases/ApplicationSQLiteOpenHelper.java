package com.aizoban.naitokenzai.controllers.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aizoban.naitokenzai.NaitoKenzaiApplication;
import com.aizoban.naitokenzai.models.Chapter;
import com.aizoban.naitokenzai.models.databases.FavouriteManga;
import com.aizoban.naitokenzai.models.databases.RecentChapter;
import com.aizoban.naitokenzai.models.downloads.DownloadChapter;
import com.aizoban.naitokenzai.models.downloads.DownloadManga;
import com.aizoban.naitokenzai.models.downloads.DownloadPage;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;

public class ApplicationSQLiteOpenHelper extends SQLiteOpenHelper {
    private static ApplicationSQLiteOpenHelper sInstance;

    public ApplicationSQLiteOpenHelper(Context context) {
        super(context, ApplicationContract.DATABASE_NAME, null, ApplicationContract.DATABASE_VERSION);
    }

    public static synchronized ApplicationSQLiteOpenHelper getInstance() {
        if (sInstance == null) {
            sInstance = new ApplicationSQLiteOpenHelper(NaitoKenzaiApplication.getInstance());
        }

        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Cupboard applicationCupboard = constructCustomCupboard();
        applicationCupboard.withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cupboard applicationCupboard = constructCustomCupboard();
        applicationCupboard.withDatabase(db).upgradeTables();
    }

    private Cupboard constructCustomCupboard() {
        Cupboard customCupboard = new CupboardBuilder().build();
        customCupboard.register(Chapter.class);
        customCupboard.register(FavouriteManga.class);
        customCupboard.register(RecentChapter.class);
        customCupboard.register(DownloadManga.class);
        customCupboard.register(DownloadChapter.class);
        customCupboard.register(DownloadPage.class);

        return customCupboard;
    }
}
