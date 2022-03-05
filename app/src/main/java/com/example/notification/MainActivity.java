package com.example.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button notifyBtn;
    private Button updateBtn;
    private Button cancelBtn;

    private NotificationManager mNotificationManager;
    private final String CHANNEL_ID = BuildConfig.APPLICATION_ID;

    private final static int NOTIF_ID = 0;

    private final static String NOTIF_GUIDE = "https://developer.android.com/design/patterns/notifications.html";

    private final static String UPDATE_EVENT = "UPDATE_EVENT";

    //mendaftarkan receiver
    private NotificationReceiver mNotifReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notifyBtn = findViewById(R.id.notify_btn);
        updateBtn = findViewById(R.id.update_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        mNotifReceiver = new NotificationReceiver();

        //kalo UPdATE_EVENT ke trigger, dia harus nyari notif receiver
        registerReceiver(mNotifReceiver, new IntentFilter(UPDATE_EVENT));

        //buat notification managernya
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel-name", NotificationManager.IMPORTANCE_DEFAULT);
            //NotificationChannel channel = new NotificationChannel(parameter, notification name, tingkat importance
            mNotificationManager.createNotificationChannel(channel);
        }

        notifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //event nya
                sendNotification();

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //event nya
                updateNotification();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //event nya
                cancelNotification();
            }
        });

        //mengaktifkan tombol notify dulu, karena gamungkin langsung bisa update n cancel
        notifyBtn.setEnabled(true);
        updateBtn.setEnabled(false);
        cancelBtn.setEnabled(false);



//fungsi dibuat masing"
    }
    public void sendNotification() {
// menampilkan notifnya
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        //set title, set content text, set icon
        builder.setContentTitle("You've been notified!");
        builder.setContentText("This is the notification text");
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        //biar ketika notif di klik, masuknya ke aplikasinya, buat intent
        Intent contentIntent = new Intent(getApplicationContext(), MainActivity.class);
        //mau ke activity main (nama kelasnya)
        //contentIntent gabisa langsung dipake, harus di parse pendingIntent
        PendingIntent pendingContentIntent = PendingIntent.getActivity(getApplicationContext(),
                NOTIF_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //diset ke builder nya dengan parsing pendingContentIntent
        builder.setContentIntent(pendingContentIntent);


        //tombol learn more
        Intent learnMoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NOTIF_GUIDE));
        PendingIntent pendingLearnMoreIntent = PendingIntent.getActivity(getApplicationContext(),
                NOTIF_ID, learnMoreIntent, PendingIntent.FLAG_ONE_SHOT);
        //FLAG_ONE_SHOT karena ingin diklik sekali aja
        builder.addAction(R.drawable.ic_notification, "LEARN MORE", pendingLearnMoreIntent);
        //R.drawable.ic_notification pelengkap aja, ga muncul di android versi terbaru


        Intent updateIntent = new Intent(UPDATE_EVENT);
        PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(getApplicationContext(), NOTIF_ID,
                updateIntent, PendingIntent.FLAG_ONE_SHOT);
        builder.addAction(R.drawable.ic_notification, "Update", pendingUpdateIntent);


        Notification notification = builder.build();
        //menampilkan notifikasi, tugas managernya
        //setiap notifikasi perlu id karena beda, tp disini selalu menampilkan notif sama jd id 1 aja
        mNotificationManager.notify(NOTIF_ID, notification);

        notifyBtn.setEnabled(false);
        updateBtn.setEnabled(true);
        cancelBtn.setEnabled(true);
    }

    public void updateNotification() {
        // menampilkan notifnya
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        //set title, set content text, set icon
        builder.setContentTitle("You've been notified!");
        builder.setContentText("This is the notification text");
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //PRIORITY dibuat DEFAULT biar urutan notifikasi tidak diatas meski udah di update


        //menambah gambar
        Bitmap mascotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_avengers);
        //notification builder diset style nya
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(mascotBitmap).
                setBigContentTitle("This notification has been updated"));


        //biar ketika notif di klik, masuknya ke aplikasinya, buat intent
        Intent contentIntent = new Intent(getApplicationContext(), MainActivity.class);
        //mau ke activity main (nama kelasnya)
        //contentIntent gabisa langsung dipake, harus di parse pendingIntent
        PendingIntent pendingContentIntent = PendingIntent.getActivity(getApplicationContext(), NOTIF_ID,
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //diset ke builder nya dengan parsing pendingContentIntent
        builder.setContentIntent(pendingContentIntent);


        Intent learnMoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NOTIF_GUIDE));
        PendingIntent pendingLearnMoreIntent = PendingIntent.getActivity(getApplicationContext(),
                NOTIF_ID, learnMoreIntent, PendingIntent.FLAG_ONE_SHOT);
        //FLAG_ONE_SHOT karena ingin diklik sekali aja
        builder.addAction(R.drawable.ic_notification, "LEARN MORE", pendingLearnMoreIntent);


        Notification notification = builder.build();
        //menampilkan notifikasi, tugas managernya
        //setiap notifikasi perlu id karena beda, tp disini selalu menampilkan notif sama jd id 1 aja
        mNotificationManager.notify(NOTIF_ID, notification);


        notifyBtn.setEnabled(false);
        updateBtn.setEnabled(false);
        cancelBtn.setEnabled(true);
    }

    public void cancelNotification() {
        mNotificationManager.cancel(NOTIF_ID);

        notifyBtn.setEnabled(true);
        updateBtn.setEnabled(false);
        cancelBtn.setEnabled(false);

    }
    //membuat receiver broadcast pada tombol update
    public class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //cek update event apakah sama
            if (intent.getAction() == UPDATE_EVENT){
                //panggil fungsi update
                updateNotification();
            }
        }
    }
}

