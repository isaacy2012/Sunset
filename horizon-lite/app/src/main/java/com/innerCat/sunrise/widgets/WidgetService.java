package com.innerCat.sunrise.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        System.out.println("Here");
        return new DataProvider(this, intent);
    }
}

