package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.OnPageChange;

public class CrimePagerActivity extends FragmentActivity implements CrimeFragment.Callbacks {
    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    ViewPager mViewPager;

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);
        ButterKnife.inject(this);

        final ArrayList<Crime> crimes = CrimeLab.get(this).getCrimes();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return crimes.size();
            }
            @Override
            public Fragment getItem(int pos) {
                UUID crimeId =  crimes.get(pos).getId();
                return CrimeFragment.newInstance(crimeId);
            }
        }); 

        UUID crimeId = (UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        for (int i = 0; i < crimes.size(); i++) {
            if (crimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            } 
        }
    }

    @OnPageChange(R.id.viewPager)
    public void onViewPagerPageChange(int pos) {
        String title = CrimeLab.get(this).getCrimes().get(pos).getTitle();
        setTitle(title);
    }

    public void onCrimeUpdated(Crime crime) {
        // do nothing        
    }
}
