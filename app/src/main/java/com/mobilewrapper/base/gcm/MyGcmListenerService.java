/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobilewrapper.base.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.mobilewrapper.base.R;
import com.mobilewrapper.base.SplashActivity;
import com.mobilewrapper.base.WrapperApplication;
import com.mobilewrapper.base.gcm.beans.LocalDBBeans;
import com.mobilewrapper.base.gcm.beans.PushMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        PushMessage msg = new Gson().fromJson(message, PushMessage.class);

        Realm realm = Realm.getInstance(this);
        RealmQuery<LocalDBBeans> query = realm.where(LocalDBBeans.class);
        query.equalTo("id", msg.getId());
        RealmResults<LocalDBBeans> result = query.findAll();

        if (result.size() == 0) {
            realm.beginTransaction();
            LocalDBBeans localDBBeans = realm.createObjectFromJson(LocalDBBeans.class, message);
            realm.commitTransaction();

            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */


            sendNotification(msg);
            // [END_EXCLUDE]
        } else {
            // something wrong
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(PushMessage message) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra(WrapperApplication.EXTRA_SERIAL_PUSHMSG, message);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Style style = null;
        if(message.getThumb() == null || message.getThumb().equals("")) {
            style = new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(message.getDescription()));
        }
        else {
            Bitmap remote_picture = null;
            try {
                remote_picture = BitmapFactory.decodeStream((InputStream) new URL(message.getThumb()).getContent());
            } catch (IOException e) {

            }
            style = new NotificationCompat.BigPictureStyle().bigPicture(remote_picture)
                    .setSummaryText(Html.fromHtml(message.getDescription()));
        }

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setTicker(message.getTitle())
                .setContentTitle(message.getTitle())
                .setContentText(message.getDescription())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(style)
                .setContentIntent(pendingIntent);

        if(style instanceof NotificationCompat.BigPictureStyle) {
            notificationBuilder.setContentText(getString(R.string.notification_bigPictureContentText));
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
