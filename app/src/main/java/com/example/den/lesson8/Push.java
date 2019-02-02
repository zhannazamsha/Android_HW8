package com.example.den.lesson8;

import android.content.Context;

import com.onesignal.OneSignal;

public class Push {

    public static void push(Context context){
        OneSignal.startInit(context)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.setEmail("example@domain.com");
    }
}
