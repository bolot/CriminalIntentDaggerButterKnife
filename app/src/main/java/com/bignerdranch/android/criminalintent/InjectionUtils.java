package com.bignerdranch.android.criminalintent;

import android.content.Context;

public class InjectionUtils {

    public static void injectClass(Context context) {
        injectClass(context, context);
    }

    public static void injectClass(Context context, Object obj) {
        ((CriminalIntentApplication) context.getApplicationContext()).inject(obj);
    }

    public static <T> T get(Context context, Class<T> objectClass) {
        return ((CriminalIntentApplication) context.getApplicationContext()).get(objectClass);
    }
}