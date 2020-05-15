package com.vuducminh.nicefood.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.vuducminh.nicefood.model.AddonModel;
import com.vuducminh.nicefood.model.CategoryModel;
import com.vuducminh.nicefood.model.FoodModel;
import com.vuducminh.nicefood.model.SizeModel;
import com.vuducminh.nicefood.model.TokenModel;
import com.vuducminh.nicefood.model.UserModel;
import com.vuducminh.nicefood.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class Common {
    public static final String USER_REFERENCES = "Users";
    public static UserModel currentUser;
    public static CategoryModel categorySelected;
    public static FoodModel selectedFood;
    public static String currentToken = "";
    public static final String RESTAURANT_REF = "Restaurant";

    public static String formatPrice(double price) {
        if (price != 0) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            df.setRoundingMode(RoundingMode.UP);
            String finalPrice = new StringBuilder(df.format(price)).toString();
            return finalPrice.replace(".", ",");
        } else return "0,00";
    }

    public static Double calculateExtraPrice(SizeModel userSelectedSize, List<AddonModel> userSelectedAddon) {
        Double result = 0.0;

        // không có size và addon
        if (userSelectedSize == null && userSelectedAddon == null) {
            return 0.0;
        }
        // Không có size
        else if (userSelectedSize == null) {
            for (AddonModel addonModel : userSelectedAddon) {
                result += addonModel.getPrice();
            }
            return result;
        }
        //Không có addon
        else if (userSelectedAddon == null) {
            return userSelectedSize.getPrice() * 1.0;
        }

        // Có đủ cả 2
        else {
            result = userSelectedSize.getPrice() * 1.0;
            for (AddonModel addonModel : userSelectedAddon) {
                result += addonModel.getPrice();
            }
            return result;
        }
    }

    public static void setSpanString(String welcome, String name, TextView tv_user) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(welcome);
        SpannableString spannableString = new SpannableString(name);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        tv_user.setText(builder, TextView.BufferType.SPANNABLE);
    }

    public static String creteOrderNumber() {
        return new StringBuilder()
                .append(System.currentTimeMillis())        // Get current time in mini giây
                .append(Math.abs(new Random().nextInt()))  // Add random number to block same order at same time
                .toString();

    }

    public static String getDateOfWeek(int i) {
        switch (i) {
            case 1: {
                return "Monday";
            }
            case 2: {
                return "Tuesday";
            }
            case 3: {
                return "Wednesday";
            }
            case 4: {
                return "Thursday";
            }
            case 5: {
                return "Friday";
            }
            case 6: {
                return "Saturday";
            }
            case 7: {
                return "Sunday";
            }
            default: {
                return "Unknown";
            }
        }
    }

    public static String convertStatusToText(int orderStatus) {
        switch (orderStatus) {
            case 0: {
                return "Placed";
            }
            case 1: {
                return "Shipping";
            }
            case 2: {
                return "Shipped";
            }
            case -1: {
                return "Cancelled";
            }
            default:
                return "Unknown";
        }
    }

    public static void showNotification(Context context, int id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        String NOTIFICATION_CHANNEL_ID = "minh_vu_nice_food_java";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Nice Food Java", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Nice Food Java");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_restaurant_menu_black_24dp));

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

    public static void updateToken(Context context, String newToken) {
        FirebaseDatabase.getInstance()
                .getReference(CommonAgr.TOKEN_REF)
                .child(Common.currentUser.getUid())
                .setValue(new TokenModel(Common.currentUser.getPhone(), newToken))
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static String createTopicOrder() {
        return new StringBuilder("/topics/new_order").toString();
    }
}
