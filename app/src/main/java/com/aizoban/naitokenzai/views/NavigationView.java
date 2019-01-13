package com.aizoban.naitokenzai.views;

import android.content.Context;

public interface NavigationView {
    public void initializeListView();

    public void initializeSourceTextView(String source);

    public void setThumbnail(String url);

    public void highlightPosition(int position);

    public Context getContext();
}
