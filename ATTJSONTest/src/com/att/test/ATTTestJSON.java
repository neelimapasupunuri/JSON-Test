package com.att.test;

/*
 * Developer: Sri Naga Sarvani Jakkula
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ATTTestJSON {

	public static final String NUMBERS = "numbers";
	public static final String PROPS_FILE_NAME = "attJSONTest.properties";
	public static final String HTTP_GET_URL = "http_Get_URL";

	public static void main(String[] args) throws JSONException, IOException {
		String url = getURL();
		if(url == null || url.trim().isEmpty()){
			System.out.println("URL cannot be null or empty.");
			return;
		}
		
		JSONArray response = getJSONResponseFromURL(url);
		if(response == null){
			return;
		}
		
		process(response);
	}

	private static String getURL() {
		String url = "";
		Scanner scanner = new Scanner(System.in);
		url = getURLFromProperties();
		if (url == null || url.trim().isEmpty()) {
			System.out.println("Please Enter the HTTP GET URL to read the JSON stream: ");
			url = scanner.next();
			scanner.close();
		}
		return url;
	}

	public static JSONArray getJSONResponseFromURL(String url) {
		JSONArray jsonarray = null;
		try (InputStream is = new URL(url).openStream()) {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			jsonarray = new JSONArray(jsonText);
		} catch (IOException | JSONException e) {
			System.out.println("Unable to get/process response from HTTP URL due to the exception: " + e.getMessage());
		}
		return jsonarray;
	}

	public static void process(JSONArray response) {
		try {
			int runningTotal = 0;
			for (int i = 0; i < response.length(); i++) {
				JSONObject record = response.getJSONObject(i);
				JSONArray numbers = record.getJSONArray(NUMBERS);
				int arraySum = 0;
				for (int j = 0; j < numbers.length(); j++) {
					arraySum += numbers.getInt(j);
				}
				System.out.println("Numbers: " + record.getJSONArray(NUMBERS) + "; ArraySum: " + arraySum);
				runningTotal += arraySum;
			}
			System.out.println("\nRunning Total: " + runningTotal);
		} catch (JSONException ex) {
			System.out.println("Unable to process JSON response due to the exception: " + ex.getMessage());
		}
	}

	private static String readAll(BufferedReader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	public static String getURLFromProperties() {
		String url = null;
		Properties props = new Properties();
		try (InputStream input = ATTTestJSON.class.getClassLoader().getResourceAsStream(PROPS_FILE_NAME)) {
			props.load(input);
			url = props.getProperty(HTTP_GET_URL);
		} catch (IOException | NullPointerException ex) {
			System.out.println("Unable to retrieve the value for URL from properties file.");
		}
		return url;
	}
}