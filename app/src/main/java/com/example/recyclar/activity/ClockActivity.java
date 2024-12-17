package com.example.recyclar.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.recyclar.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClockActivity extends AppCompatActivity {
    private TextView tvPomodoroTitle;
    private TextView tvPomodoroTime;
    private Button btnStartPause;
    private ImageView btn_exit;
    private CountDownTimer countDownTimer;
    private long remainingTime; // 剩余时间（秒）
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        // 获取传递的番茄钟数据
        String title = getIntent().getStringExtra("matters");
        int time = getIntent().getIntExtra("time", 25);

        // 初始化 UI
        tvPomodoroTitle = findViewById(R.id.tvPomodoroTitle);
        tvPomodoroTime = findViewById(R.id.tvPomodoroTime);
        btnStartPause = findViewById(R.id.btnStartPause);
        btn_exit = findViewById(R.id.btn_exit);
        // 设置番茄钟标题和时间
        tvPomodoroTitle.setText(title);
        remainingTime = time * 60 * 1000; // 转换为毫秒

        // 更新显示的倒计时
        updateTimeDisplay();

        // 设置开始/暂停按钮点击事件
        btnStartPause.setOnClickListener(v -> {
            if (isRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        btn_exit.setOnClickListener(v->{
            pauseTimer();
            finish();
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(remainingTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
                updateTimeDisplay();
            }

            @Override
            public void onFinish() {
                Toast.makeText(ClockActivity.this, "番茄钟结束！", Toast.LENGTH_SHORT).show();
                triggerVibration();
                showNotification();
                updatePomodoroFocusCount();
            }
        }.start();

        isRunning = true;
        btnStartPause.setText("暂停");
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isRunning = false;
        btnStartPause.setText("继续");
    }

    private void updateTimeDisplay() {
        int minutes = (int) (remainingTime / 1000 / 60);
        int seconds = (int) (remainingTime / 1000 % 60);
        tvPomodoroTime.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void triggerVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android 8.0及以上使用新的震动API
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Android 8.0以下使用旧的震动API
                vibrator.vibrate(1000);
            }
        }
    }

    private void showNotification() {
        String channelId = "pomodoro_channel";
        String channelName = "Pomodoro Notifications";

        // 创建 NotificationManager
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 创建 NotificationChannel（适用于 Android 8.0+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("通知用户番茄钟完成");
            notificationManager.createNotificationChannel(channel);
        }

        // 设置点击通知时打开的活动
        Intent intent = new Intent(this, ClockActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 构建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // 替换为有效图标
                .setContentTitle("番茄钟完成")
                .setContentText("您的专注时间已结束！")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // 点击后自动移除通知

        // 显示通知
        Log.d("NotificationDebug", "显示通知...");
        notificationManager.notify(1, builder.build());
    }

    private void updatePomodoroFocusCount() {
        // 获取当前日期（可以使用简单的日期格式）
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // 获取 SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("FocusTimes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 获取今天的专注次数
        int currentFocusCount = sharedPreferences.getInt(todayDate, 0);

        // 增加专注次数
        currentFocusCount++;

        // 保存更新后的次数到 SharedPreferences
        editor.putInt(todayDate, currentFocusCount);
        editor.putInt("2024-12-15", 6);
        editor.putInt("2024-12-14", 3);
        editor.putInt("2024-12-13", 7);
        editor.putInt("2024-12-12", 1);
        editor.apply();

        // 显示更新后的专注次数
        Toast.makeText(this, "今日专注次数：" + currentFocusCount, Toast.LENGTH_SHORT).show();
    }
}
