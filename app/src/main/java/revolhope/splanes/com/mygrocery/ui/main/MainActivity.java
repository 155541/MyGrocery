package revolhope.splanes.com.mygrocery.ui.main;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.helpers.repository.AppRepository;
import revolhope.splanes.com.mygrocery.ui.main.grocery.GroceryFragment;

public class MainActivity extends AppCompatActivity{

    public static final String ITEMS = "PendingItems";
    private List<Item> pendingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewPager viewPager = findViewById(R.id.view_pager);
        final TabLayout tabs = findViewById(R.id.tabs);
        TextView textViewUser = findViewById(R.id.textViewUser);

        if (AppRepository.getAppUser() != null &&
            AppRepository.getAppUser().getDisplayName() != null) {
            textViewUser.setText(AppRepository.getAppUser().getDisplayName());
        }

        SectionsPagerAdapter sectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(1);

        tabs.setupWithViewPager(viewPager);
        int iconDraw = 0;
        for (int i = 0 ; i < tabs.getTabCount() ; i++) {
            switch (i) {
                case 0: iconDraw = R.drawable.ic_historic; break;
                case 1: iconDraw = R.drawable.ic_grocery; break;
                case 2: iconDraw = R.drawable.ic_notification; break;
            }
            Objects.requireNonNull(tabs.getTabAt(i)).setIcon(iconDraw);
        }

        if (getIntent().hasExtra(ITEMS)) {
            pendingItems = Arrays.asList((Item[])getIntent().getSerializableExtra(ITEMS));
        }
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final int[] titles = new int[] {
                R.string.tab_title_1, R.string.tab_title_2, R.string.tab_title_3
        };

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(position);
                case 1:
                    return GroceryFragment.newInstance(pendingItems);
                case 2:
                    return PlaceholderFragment.newInstance(position);
            }
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return getString(titles[position]);
        }
    }
}