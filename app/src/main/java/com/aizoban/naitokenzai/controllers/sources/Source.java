package com.aizoban.naitokenzai.controllers.sources;

import com.aizoban.naitokenzai.models.Chapter;
import com.aizoban.naitokenzai.models.Manga;
import com.aizoban.naitokenzai.utils.wrappers.RequestWrapper;

import java.util.List;

import rx.Observable;

public interface Source {
    public Observable<String> getName();

    public Observable<String> getBaseUrl();

    public Observable<String> getInitialUpdateUrl();

    public Observable<List<String>> getGenres();

    public Observable<UpdatePageMarker> pullLatestUpdatesFromNetwork(UpdatePageMarker newUpdate);

    public Observable<Manga> pullMangaFromNetwork(RequestWrapper request);

    public Observable<List<Chapter>> pullChaptersFromNetwork(RequestWrapper request);

    public Observable<String> pullImageUrlsFromNetwork(RequestWrapper request);

    public Observable<String> recursivelyConstructDatabase(String url);
}
