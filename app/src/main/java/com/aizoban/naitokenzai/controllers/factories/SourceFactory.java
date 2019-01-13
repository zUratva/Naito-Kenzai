package com.aizoban.naitokenzai.controllers.factories;

import com.aizoban.naitokenzai.controllers.sources.English_MangaEden;
import com.aizoban.naitokenzai.controllers.sources.English_MangaHere;
import com.aizoban.naitokenzai.controllers.sources.English_MangaReader;
import com.aizoban.naitokenzai.controllers.sources.Italian_MangaEden;
import com.aizoban.naitokenzai.controllers.sources.Source;
import com.aizoban.naitokenzai.controllers.sources.Spanish_MangaHere;
import com.aizoban.naitokenzai.utils.PreferenceUtils;

public class SourceFactory {
    public static Source constructSourceFromPreferences() {
        String sourceName = PreferenceUtils.getSource();

        Source currentSource = null;

        if (sourceName.equalsIgnoreCase(English_MangaEden.NAME)) {
            currentSource = new English_MangaEden();
        } else if (sourceName.equalsIgnoreCase(English_MangaHere.NAME)) {
            currentSource = new English_MangaHere();
        } else if (sourceName.equalsIgnoreCase(English_MangaReader.NAME)) {
            currentSource = new English_MangaReader();
        } else if (sourceName.equalsIgnoreCase(Italian_MangaEden.NAME)) {
            currentSource = new Italian_MangaEden();
        } else if (sourceName.equalsIgnoreCase(Spanish_MangaHere.NAME)) {
            currentSource = new Spanish_MangaHere();
        } else {
            currentSource = new English_MangaEden();
        }

        return currentSource;
    }

    public static Source constructSourceFromName(String sourceName) {
        Source currentSource = null;

        if (sourceName.equalsIgnoreCase(English_MangaEden.NAME)) {
            currentSource = new English_MangaEden();
        } else if (sourceName.equalsIgnoreCase(English_MangaHere.NAME)) {
            currentSource = new English_MangaHere();
        } else if (sourceName.equalsIgnoreCase(English_MangaReader.NAME)) {
            currentSource = new English_MangaReader();
        } else if (sourceName.equalsIgnoreCase(Italian_MangaEden.NAME)) {
            currentSource = new Italian_MangaEden();
        } else if (sourceName.equalsIgnoreCase(Spanish_MangaHere.NAME)) {
            currentSource = new Spanish_MangaHere();
        } else {
            currentSource = new English_MangaEden();
        }

        return currentSource;
    }

    public static Source constructSourceFromUrl(String url) {
        Source currentSource = null;

        if (url.contains(English_MangaEden.BASE_URL)) {
            currentSource = new English_MangaEden();
        } else if (url.contains(English_MangaHere.BASE_URL)) {
            currentSource = new English_MangaHere();
        } else if (url.contains(English_MangaReader.BASE_URL)) {
            currentSource = new English_MangaReader();
        } else if (url.contains(Italian_MangaEden.BASE_URL)) {
            currentSource = new Italian_MangaEden();
        } else if (url.contains(Spanish_MangaHere.BASE_URL)) {
            currentSource = new Spanish_MangaHere();
        } else {
            currentSource = new English_MangaEden();
        }


        return currentSource;
    }
}
