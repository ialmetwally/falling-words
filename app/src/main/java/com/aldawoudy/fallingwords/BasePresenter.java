package com.aldawoudy.fallingwords;

import java.lang.ref.WeakReference;

/**
 * Created by Ismail on 3/12/16.
 */
public class BasePresenter<V> {

    private WeakReference<V> viewRef;

    public void attachView(V view) {
        viewRef = new WeakReference<>(view);
    }

    public V getView() {
        return viewRef == null ? null : viewRef.get();
    }

    public boolean isViewAttached() {
        return viewRef != null && viewRef.get() != null;
    }

    public void detachView() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }
}
