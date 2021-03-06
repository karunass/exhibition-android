/*
 * Copyright 2016 Promtech. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.gkpromtech.exhibition.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.List;

import ru.gkpromtech.exhibition.R;
import ru.gkpromtech.exhibition.net.RssParser;
import ru.gkpromtech.exhibition.utils.AnalyticsManager;

public class NewsDetailsActivity extends ActionBarActivity {
    private List<RssParser.Item> mNewsItems;

    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        Bundle extras = getIntent().getExtras();
        mNewsItems = (List<RssParser.Item>) extras.getSerializable("items");

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new NewsDetailsCollectionPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(extras.getInt("index", 0));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.
    public class NewsDetailsCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public NewsDetailsCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new NewsDetailsObjectFragment();
            Bundle args = new Bundle();
            args.putSerializable("item", mNewsItems.get(i));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return mNewsItems.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mNewsItems.get(position).title;
        }
    }

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class NewsDetailsObjectFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
            RssParser.Item item = (RssParser.Item) getArguments().getSerializable("item");
            WebView webView = new WebView(getActivity());
            webView.loadUrl(item.link + "&mobile=true");

            AnalyticsManager.sendEvent(getActivity(), R.string.news_category, R.string.action_open, item.link);
            return webView;
        }
    }
}
