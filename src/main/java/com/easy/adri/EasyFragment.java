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

public class EasyFragment extends Fragment {

    public String TAG = "EasyFragment";
    private JavaHelpers.Callback testCallback;
    private Promise<Void> mTestStartPromise = new Promise<>();
    protected Promise<Void> mPromiseDisplayed = new Promise<>();

    public Promise promiseDisplayed() {
        return mPromiseDisplayed;
    }

    protected void registerDisplayLayout(View layout) {
        ViewHelpers.whenViewHasLayout(layout, new JavaHelpers.Callback() {
            @Override
            public void call() {
                mPromiseDisplayed.accept();
            }
        });
    }

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

        mTestStartPromise.accept(null);
    }

    public Promise testWhenStarted() {
        return mTestStartPromise;
    }
}
