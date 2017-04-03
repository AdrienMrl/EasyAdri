package com.easy.adri;

import android.icu.util.TimeUnit;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EasyFragment extends Fragment {

    public String TAG = "EasyFragment";
    private JavaHelpers.Callback testCallback;

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

    @Override public void onStart() {
        super.onStart();

        if (testCallback != null)
            testCallback.call();
        testCallback = null;
    }

    public void testWhenStarted(final JavaHelpers.Callback done) {

        testCallback = new JavaHelpers.Callback() {
            @Override
            public void call() {
                done.call();
            }
        };
    }
}
