package com.bignerdranch.android.criminalintent;

import android.app.Application;

import dagger.ObjectGraph;

public class CriminalIntentApplication extends Application {
    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        buildObjectGraph();
    }

    public void buildObjectGraph() {
        mObjectGraph = ObjectGraph.create(new CriminalIntentModule(this));
    }

    public <T> T get(Class<T> klass) {
        return mObjectGraph.get(klass);
    }

    public void inject(Object o) {
        mObjectGraph.inject(o);
    }
}
