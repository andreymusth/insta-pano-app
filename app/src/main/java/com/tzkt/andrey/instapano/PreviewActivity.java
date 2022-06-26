package com.tzkt.andrey.instapano;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tzkt.andrey.instapano.utils.BitmapUtils;
import com.tzkt.andrey.instapano.utils.NavigationUtils;

import java.util.ArrayList;

/**
 * Created by andrey on 28/01/2018.
 */

public class PreviewActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private PagerAdapter mAdapter;

    private boolean mImagesAlreadySaved = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(new PreviewPagerAdapter(getSupportFragmentManager()));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private final class PreviewPagerAdapter extends FragmentPagerAdapter {

        public PreviewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PreviewFragment.newInstance(BitmapUtils.imgs[position]);
        }

        @Override
        public int getCount() {
            return BitmapUtils.imgs.length;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                NavigationUtils.openSettings(this);
                return true;
            case R.id.action_save:
                if (!mImagesAlreadySaved) {
                    new SavingImagesAsyncTask().execute();
                } else {
                    Toast.makeText(this, getString(R.string.images_saving_error), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_instructions:
                NavigationUtils.openInstructions(this);
                return true;
            case R.id.action_share:
                if (BitmapUtils.uris == null) {
                    new SharingImagesAsyncTask().execute();
                } else {
                    showAppChooser(BitmapUtils.uris);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preview, menu);
        return true;
    }

    private final class SavingImagesAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            BitmapUtils.saveBitmaps(PreviewActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mImagesAlreadySaved = true;
            Toast.makeText(PreviewActivity.this, getString(R.string.images_saving_success), Toast.LENGTH_SHORT).show();
        }
    }

    private final class SharingImagesAsyncTask extends AsyncTask<Void, Void, ArrayList<Uri>> {

        @Override
        protected ArrayList<Uri> doInBackground(Void... voids) {
            return BitmapUtils.saveBitmaps(PreviewActivity.this);
        }

        @Override
        protected void onPostExecute(ArrayList<Uri> uris) {
            mImagesAlreadySaved = true;
            showAppChooser(uris);
        }
    }

    private void showAppChooser(ArrayList<Uri> uris) {

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        try {
            PreviewActivity.this.startActivity(Intent.createChooser(intent, PreviewActivity.this.getString(R.string.image_share_title)));
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        BitmapUtils.uris = null;
    }
}
