package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static void sendRequestMessage(final String address,
			final HttpCallBackListener listener) {
		new Thread() {
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder builder = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
					if (listener != null) {
						String response = builder.toString();
						if (response.startsWith("\ufeff")) {
							response = response.substring(1);
						}

						listener.onFinish(response);
					}

				} catch (Exception e) {
					e.printStackTrace();
					if (listener != null) {
						listener.onError(e);
					}

				} finally {
					if (connection != null) {
						connection.disconnect();
					}

				}
			}
		}.start();

	}
}
