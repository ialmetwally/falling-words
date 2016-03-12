package com.aldawoudy.fallingwords.utlis;

import android.content.Context;
import android.support.annotation.RawRes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Ismail on 3/12/16.
 */
public class RawDataReader {

    private final Context context;

    public RawDataReader(Context context) {
        this.context = context;
    }

    public Observable<String> readString(@RawRes final int rawId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    return Observable.just(readStringStringBlocking(rawId));
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private String readStringStringBlocking(@RawRes int rawId) throws IOException {
        InputStream is = context.getResources().openRawResource(rawId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        int n;
        while ((n = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, n);
        }
        is.close();
        return writer.toString();
    }
}
