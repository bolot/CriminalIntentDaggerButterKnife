package com.bignerdranch.android.criminalintent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                CrimePagerActivity.class,
                CrimeFragment.class,
                CrimeListFragment.class
        }
)
public class CriminalIntentModule {
    private final CriminalIntentApplication mApp;

    public CriminalIntentModule(CriminalIntentApplication app) {
        mApp = app;
    }

    @Provides @Singleton
    CrimeLab provideCrimeLab() {
        return CrimeLab.get(mApp);
    }
}
