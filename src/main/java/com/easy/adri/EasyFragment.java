package com.easy.adri;

import android.icu.util.TimeUnit;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telecom.Call;
import android.util.Log;

public class EasyFragment extends Fragment {

    public String TAG = "EasyFragment";

    public void pushFragment(EasyFragment fragment, int containerId, String TAG) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment, TAG);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

    }

    public void onTopOfStack() {
        Log.d("adrien", "Now on top of the stack :)");
    }

    public boolean guardAttached() {
        return getActivity() == null || !isAdded();
    }
}
