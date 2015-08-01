package com.coolweather.app.activity;

import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.example.androidfirstline_coolweather.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {

	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView weatherDespText;
	private TextView publishTimeText;
	private TextView currentTimeText;

	private Button switchCity;
	private Button refreshWeather;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		publishTimeText = (TextView) findViewById(R.id.publish_text);
		currentTimeText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		String countyCode = getIntent().getStringExtra("county_code");

		if (!TextUtils.isEmpty(countyCode)) {
			publishTimeText.setText("同步中....");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryCountyWeatherCode(countyCode);
		} else {
			showWeather();
		}
		switchCity.setOnClickListener(WeatherActivity.this);
		refreshWeather.setOnClickListener(WeatherActivity.this);

	}

	private void showWeather() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(preferences.getString("city_name", ""));
		temp1Text.setText(preferences.getString("temp1", ""));
		temp2Text.setText(preferences.getString("temp2", ""));
		weatherDespText.setText(preferences.getString("weatherDesp", ""));
		publishTimeText.setText(preferences.getString("publishTime", ""));
		currentTimeText.setText(preferences.getString("current_time", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);

	}

	private void queryCountyWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");

	}

	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");

	}

	private void queryFromServer(String address, final String type) {
		HttpUtil.sendRequestMessage(address, new HttpCallBackListener() {

			@Override
			public void onFinish(String responseMsg) {
				Log.e("WeatherActivity", responseMsg);
				if ("countyCode".equals(type)) {
					String[] array = responseMsg.split("\\|");
					if (array != null && array.length == 2) {
						String weatherCode = array[1];
						queryWeatherInfo(weatherCode);
					}
				} else if ("weatherCode".equals(type)) {
					boolean result;
					result = Utility.handleWeatherInfo(WeatherActivity.this,
							responseMsg);
					if (result) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								showWeather();

							}
						});
					}
				}

			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						publishTimeText.setText("同步失败");
					}
				});

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(WeatherActivity.this,
					ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();

			break;
		case R.id.refresh_weather:
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(WeatherActivity.this);
			String weatherCode = preferences.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}

			break;

		default:
			break;
		}

	}

}
