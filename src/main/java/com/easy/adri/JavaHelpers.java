package com.easy.adri;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by adrienmorel on 17/02/2017.
 */

public class JavaHelpers {

    public interface Callback {
        public void call();
    }

    public interface CallBackWithArg<T> {
        public void call(T arg);
    }

    static public void after(int delay, final Callback cb) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cb.call();
            }
        }, delay);
    }

}
