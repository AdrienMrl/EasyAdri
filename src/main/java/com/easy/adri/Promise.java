package com.easy.adri;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;

import java.util.Objects;
import java.util.Stack;

public class Promise<T> {

    public interface Callback<A, B> {
        public B run(A value);
    }

    JavaHelpers.CallBackWithArg<Exception> mOnError;
    Promise mNextPromise;
    Callback mCb;

    public Promise then(Callback cb) {
        mNextPromise = new Promise();
        mNextPromise.mCb = cb;
        if (mCb == null)
            mNextPromise.accept(null);
        return mNextPromise;
    }

    public Promise thenOnUI(final Activity activity, final Callback cb) {
        return then(new Callback() {
            @Override
            public Object run(final Object value) {

                final Promise promise = new Promise();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        promise.accept(cb.run(value));
                    }
                });
                return promise;
            }
        });
    }

    public void accept(final T result) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Object res = null;
                    if (mCb != null) {
                        res = mCb.run(result);
                        if (res instanceof Promise) {
                            ((Promise) res).mNextPromise = mNextPromise;
                            ((Promise) res).mOnError = mOnError;
                            mNextPromise = null;
                            mOnError = null;
                        }
                    }
                    if (mNextPromise != null)
                        mNextPromise.accept(res);
                } catch (Exception e) {
                    if (mOnError != null)
                        mOnError.call(e);
                }
            }
        });
    }

    public Promise delay(final long delay) {

        return new Promise<Void>()
                .then(new Promise.Callback() {
                    @Override
                    public Object run(Object value) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {}
                        return null;
                    }
                });
    }

    public void reject(Exception err) {
        if (mOnError != null)
            mOnError.call(err);
        else if (mNextPromise != null)
            mNextPromise.reject(err);
    }

    // todo allow catch chaining
    public void fail(JavaHelpers.CallBackWithArg<Exception> onErr) {
        mOnError = onErr;
    }

}
