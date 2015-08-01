package com.coolweather.app.service;

import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				upDateWeatherInfo();
			}

		}).start();

		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int hours = 8 * 60 * 60 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + hours;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent intent2 = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,
				intent2);
		return super.onStartCommand(intent, flags, startId);
	}

	private void upDateWeatherInfo() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = preferences.getString("weather_code", "");
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		HttpUtil.sendRequestMessage(address, new HttpCallBackListener() {

			@Override
			public void onFinish(String responseMsg) {
				Utility.handleWeatherInfo(AutoUpdateService.this, responseMsg);

			}

			@Override
			public void onError(Exception e) {

			}
		});

	}
}
