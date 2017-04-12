package com.easy.adri;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.telecom.Call;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class EasyActivity extends FragmentActivity {

    public JavaHelpers.CallBackWithArg<Boolean> permissionResultCb;

    private FrameLayout mLoadingLayout;

    // setting locale, for testing only
    public void setLocale(String language, String country) {

        Locale locale = new Locale(language, country);
        // here we update locale for date formatters
        Locale.setDefault(locale);
        // here we update locale for app resources
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public void hideStatusBar() {

        // make the game fullscreen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        int index = getSupportFragmentManager().getBackStackEntryCount() - 1;

        if (index < 0)
            return;

        FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
        String tag = backEntry.getName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof EasyFragment)
            ((EasyFragment) fragment).onTopOfStack();
    }

    public void setHideStatusBarAutomatically() {

        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideStatusBar();
                        }
                    }, 2000);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult (int requestCode,
                                     String[] permissions,
                                     int[] grantResults) {

        if (permissionResultCb != null)
            permissionResultCb.call(grantResults[0] == PERMISSION_GRANTED);
        permissionResultCb = null;
    }

    public void askForPermission(String permission, JavaHelpers.CallBackWithArg<Boolean> granted) {

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 0);
            permissionResultCb = granted;
        }
    }

    public void setLoading(Integer color) {

        FrameLayout frameLayout = (FrameLayout) findViewById(android.R.id.content);

        mLoadingLayout = new FrameLayout(this);
        mLoadingLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ProgressBar progressBar = new ProgressBar(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(100, 100);
        lp.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(lp);

        View background = new FrameLayout(this);
        int backgroundColor = color == null ? 0xFFFFFFFF : color;
        background.setBackgroundColor(backgroundColor);
        background.setAlpha(0.6f);
        mLoadingLayout.addView(progressBar);
        mLoadingLayout.addView(background);
        frameLayout.addView(mLoadingLayout);
    }

    public void endLoading() {

        FrameLayout frameLayout = (FrameLayout) findViewById(android.R.id.content);
        frameLayout.removeView(mLoadingLayout);
        mLoadingLayout = null;
    }
}
