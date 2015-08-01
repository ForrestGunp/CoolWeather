package com.coolweather.app.util;

import android.test.AndroidTestCase;
import android.util.Log;

public class HttpUtilTest extends AndroidTestCase {
	public void connectionUrl() {
		String address = "http://www.weather.com.cn/data/list3/city.xml";
		HttpUtil.sendRequestMessage(address, new HttpCallBackListener() {

			@Override
			public void onFinish(String responseMsg) {
				

				Log.e("TestUtil", responseMsg);
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub

			}
		});
	}

}
