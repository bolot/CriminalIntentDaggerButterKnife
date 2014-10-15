package com.bignerdranch.android.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "CRIME_ID";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTACT = 2;

    Crime mCrime;
    @InjectView(R.id.crime_title)
    EditText mTitleField;
    @InjectView(R.id.crime_date)
    Button mDateButton;
    @InjectView(R.id.crime_solved)
    CheckBox mSolvedCheckBox;
    @InjectView(R.id.crime_imageButton)
    ImageButton mPhotoButton;
    @InjectView(R.id.crime_imageView)
    ImageView mPhotoView;
    @InjectView(R.id.crime_suspectButton)
    Button mSuspectButton;
    Callbacks mCallbacks;
    @Inject
    CrimeLab mCrimeLab;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectionUtils.injectClass(getActivity(), this);
        
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = mCrimeLab.getCrime(crimeId);

        setHasOptionsMenu(true);
    }

    public void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    @Override
    @TargetApi(11)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
        ButterKnife.inject(this, v);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        mTitleField.setText(mCrime.getTitle());

        mSolvedCheckBox.setChecked(mCrime.isSolved());

        updateDate();

        // if camera is not available, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            mPhotoButton.setEnabled(false);
        }

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        return v; 
    }

    @OnTextChanged(R.id.crime_title)
    public void onTitleTextChanged(CharSequence c, int start, int before, int count) {
        mCrime.setTitle(c.toString());
        mCallbacks.onCrimeUpdated(mCrime);
    }

    @OnCheckedChanged(R.id.crime_solved)
    public void onSolvedCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // set the crime's solved property
        mCrime.setSolved(isChecked);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    @OnClick(R.id.crime_date)
    public void onDateButtonClick(View v) {
        FragmentManager fm = getActivity()
                .getSupportFragmentManager();
        DatePickerFragment dialog = DatePickerFragment
                .newInstance(mCrime.getDate());
        dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
        dialog.show(fm, DIALOG_DATE);
    }

    @OnClick(R.id.crime_imageButton)
    public void onPhotoButtonClick(View v) {
        // launch the camera activity
        Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
        startActivityForResult(i, REQUEST_PHOTO);
    }

    @OnClick(R.id.crime_imageView)
    public void onPhotoViewClick(View v) {
        Photo p = mCrime.getPhoto();
        if (p == null)
            return;

        FragmentManager fm = getActivity()
                .getSupportFragmentManager();
        String path = getActivity()
                .getFileStreamPath(p.getFilename()).getAbsolutePath();
        ImageFragment.createInstance(path)
                .show(fm, DIALOG_IMAGE);
    }

    @OnClick(R.id.crime_suspectButton)
    public void onSuspectButtonClick(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, REQUEST_CONTACT);
    }

    @OnClick(R.id.crime_reportButton)
    public void onReportButtonClick(View v) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
        i = Intent.createChooser(i, getString(R.string.send_report));
        startActivity(i);
    }

    private void showPhoto() {
        // (re)set the image button's image based on our photo
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity()
                .getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }
        mPhotoView.setImageDrawable(b);
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = DatePickerFragment.getDate(data);
            mCrime.setDate(date);
            mCallbacks.onCrimeUpdated(mCrime);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            // create a new Photo object and attach it to the crime
            String filename = data
                .getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                Photo p = new Photo(filename);
                mCrime.setPhoto(p);
                mCallbacks.onCrimeUpdated(mCrime);
                showPhoto();
            }
        } else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[] { ContactsContract.Contacts.DISPLAY_NAME_PRIMARY };
            Cursor c = getActivity().getContentResolver()
                .query(contactUri, queryFields, null, null, null);

            if (c.getCount() == 0) {
                c.close();
                return; 
            }

            c.moveToFirst();
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            mCallbacks.onCrimeUpdated(mCrime);
            mSuspectButton.setText(suspect);
            c.close();
        }
    }
    
    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    @Override
    public void onPause() {
        super.onPause();
        mCrimeLab.saveCrimes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } 
    }
}
