package com.coolweather.app.util;

public interface HttpCallBackListener {
	public void onFinish(String responseMsg);

	public void onError(Exception e);

}
