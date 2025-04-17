package com.mobile2.uts_elsid;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AuthPagerAdapter extends FragmentStateAdapter {

    public AuthPagerAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new LoginFragment() : new RegisterFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}