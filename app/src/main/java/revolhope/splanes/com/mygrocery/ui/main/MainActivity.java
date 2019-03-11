package revolhope.splanes.com.mygrocery.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.helpers.repository.AppRepository;
import revolhope.splanes.com.mygrocery.ui.main.grocery.GroceryFragment;
import revolhope.splanes.com.mygrocery.ui.main.grocery.GroceryMasterFragment;

public class MainActivity extends AppCompatActivity{

    private GroceryMasterFragment groceryMasterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textViewUser = findViewById(R.id.textViewUser);
        if (AppRepository.getAppUser() != null &&
            AppRepository.getAppUser().getDisplayName() != null) {
            textViewUser.setText(AppRepository.getAppUser().getDisplayName());
        }

        SectionsPagerAdapter sectionsPagerAdapter =
                new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        @StringRes
        private final int[] TAB_TITLES =
                new int[]{R.string.tab_title_1, R.string.tab_title_2, R.string.tab_title_3};
        private final Context mContext;

        private SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(position);
                case 1:
                    if(groceryMasterFragment == null) {
                        groceryMasterFragment = new GroceryMasterFragment();
                    }
                    //return groceryMasterFragment;
                    return new GroceryFragment();
                case 2:
                    return PlaceholderFragment.newInstance(position);
            }
            return PlaceholderFragment.newInstance(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getResources().getString(TAB_TITLES[position]);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}