package app.seamansbook.tests.services;

import static app.seamansbook.tests.MainActivity.subscribeToTopic;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;

import app.seamansbook.tests.MainActivity;
import app.seamansbook.tests.R;
import app.seamansbook.tests.RetrofitClient;
import app.seamansbook.tests.data.NotificationsDBManager;
import app.seamansbook.tests.interfaces.ApiInterface;
import app.seamansbook.tests.models.UpdateTokenRequest;
import app.seamansbook.tests.models.UpdateTokenResponse;
import retrofit2.Call;

public class NotificationService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();
        String emailType = remoteMessage.getData().getOrDefault("emailType", "message");
        String showUpdateButton = remoteMessage.getData().getOrDefault("showUpdateButton", "0");
        String additionalLink = remoteMessage.getData().getOrDefault("additionalLink", "");

        NotificationsDBManager notificationsDBManager = new NotificationsDBManager(this);
        notificationsDBManager.saveMessage(title, message, emailType, showUpdateButton, additionalLink);



        final String CHANNEL_ID = "APPLICATION_CHANNEL";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Seamansbook notifications",
                    NotificationManager.IMPORTANCE_HIGH);

            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);


            Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.splash_image)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.splash_image))
                    .setAutoCancel(true);

            Notification notification = builder.build();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            NotificationManagerCompat.from(this).notify(0, notification);
        }
    }

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);
        SharedPreferences preferences = getSharedPreferences("seamansbookMain", MODE_PRIVATE);
        preferences.edit().putString("notificationToken", newToken).apply();

        subscribeToTopic("all");
        subscribeToTopic(Locale.getDefault().getLanguage());

        String userId = preferences.getString("userId", "");
        if (!userId.equals("")) {
            ApiInterface apiService = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
            UpdateTokenRequest updateTokenRequest = new UpdateTokenRequest(userId, newToken);
            Call<UpdateTokenResponse> call = apiService.updateToken(updateTokenRequest);

            call.enqueue(new retrofit2.Callback<UpdateTokenResponse>() {
                @Override
                public void onResponse(@NonNull Call<UpdateTokenResponse> call, @NonNull retrofit2.Response<UpdateTokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("myLogs", "Token updated");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UpdateTokenResponse> call, @NonNull Throwable t) {
                    Log.d("myLogs", "Token update failed");
                }
            });

        }

        Log.d("myLogs", "Token 2: " + newToken);
    }


}