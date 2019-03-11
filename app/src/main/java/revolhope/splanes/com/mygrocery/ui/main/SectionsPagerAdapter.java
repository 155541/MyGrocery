package revolhope.splanes.com.mygrocery.ui.main;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import revolhope.splanes.com.mygrocery.R;
import revolhope.splanes.com.mygrocery.ui.main.grocery.GroceryMasterFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES =
            new int[]{R.string.tab_title_1, R.string.tab_title_2, R.string.tab_title_3};
    private final Context mContext;
    private GroceryMasterFragment groceryMasterFragment;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
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
                return groceryMasterFragment;
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