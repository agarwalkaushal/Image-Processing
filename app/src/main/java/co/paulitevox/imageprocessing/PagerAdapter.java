package co.paulitevox.imageprocessing;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by HP on 22-12-2017.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    int numTabs;

    public PagerAdapter(FragmentManager fm, int numberTabs) {
        super(fm);
        this.numTabs=numberTabs;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                chooseImage tab=new chooseImage();
                return tab;
            case 1:
                click tab1=new click();
                return tab1;
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return numTabs;
    }

}