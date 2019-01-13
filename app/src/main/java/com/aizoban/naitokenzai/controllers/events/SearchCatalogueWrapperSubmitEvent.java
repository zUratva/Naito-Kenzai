package com.aizoban.naitokenzai.controllers.events;

import com.aizoban.naitokenzai.utils.wrappers.SearchCatalogueWrapper;

public class SearchCatalogueWrapperSubmitEvent {
    private SearchCatalogueWrapper mSearchCatalogueWrapper;

    public SearchCatalogueWrapperSubmitEvent(SearchCatalogueWrapper searchCatalogueWrapper) {
        mSearchCatalogueWrapper = searchCatalogueWrapper;
    }

    public SearchCatalogueWrapper getSearchCatalogueWrapper() {
        return mSearchCatalogueWrapper;
    }
}
