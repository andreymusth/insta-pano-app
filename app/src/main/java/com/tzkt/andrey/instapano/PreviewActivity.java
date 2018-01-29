package com.tzkt.andrey.instapano;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tzkt.andrey.instapano.utils.BitmapUtils;
import com.tzkt.andrey.instapano.utils.NavigationUtils;

/**
 * Created by andrey on 28/01/2018.
 */

public class PreviewActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private PagerAdapter mAdapter;

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

        switch (itemId){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                NavigationUtils.openSettings(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preview, menu);
        return true;
    }
}
