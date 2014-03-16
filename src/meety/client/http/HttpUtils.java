package meety.client.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Build;

public abstract class HttpUtils {

	{
		disableConnectionReuseIfNecessary();
	}
	
	private void disableConnectionReuseIfNecessary(){
	    // Http connection reuse which was buggy pre-froyo
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
	        System.setProperty("http.keepAlive", "false");
	    }
	}

	private static class HttpRequestExecutor extends AsyncTask<String, Void, JSONObject>{

		@Override
		protected JSONObject doInBackground(String... params) {

			try {
				
				System.out.println("REQUEST METHOD: "+params[0]);
				String method = params[0];
				System.out.println("URL FOUND: "+params[1]);
				URL url = new URL(params[1]);
				System.out.println("HEADERS: "+params[2].toString());
				String headers = params[2];
				String payload = params[3];
			
				HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
				try{
					
					if ( headers != null ){

						JSONObject JSONHeaders = new JSONObject(headers);
						Iterator<?> keys = JSONHeaders.keys();

						while ( keys.hasNext() ){

							String key = (String) keys.next();
							String value = (String) JSONHeaders.get(key);
							urlConn.setRequestProperty(key, value);

						}
					}
					
					if ( method.toUpperCase().equals("POST") ){
						urlConn.setRequestMethod("POST");
						System.out.println("PAYLOAD IS: " + payload);
						if ( payload != null ){
							urlConn.setDoOutput(true);
							urlConn.setChunkedStreamingMode(payload.length());
							OutputStream out = new BufferedOutputStream(urlConn.getOutputStream(), payload.length());
							out.write(payload.getBytes());
						}
					}
					
					urlConn.connect();
					
					String responseMessage = urlConn.getResponseMessage();
					int responseCode = urlConn.getResponseCode();
					String responseBody = "";

					try {
						BufferedReader in = new BufferedReader(
						        new InputStreamReader(urlConn.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();
				 
						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();
						responseBody = response.toString();
					} catch(IOException e){
						e.printStackTrace();
					}
					
					JSONObject JSONResponse = new JSONObject();
					JSONResponse.put("message", responseMessage);
					JSONResponse.put("code", responseCode);
					JSONResponse.put("body", responseBody);
					return JSONResponse;
				}
				finally{
					urlConn.disconnect();
				}
				
			} catch (MalformedURLException e) {
				System.out.println("MALFORMED URL");
				e.printStackTrace();
			} catch (IOException e){
				System.out.println("IO EXCEPTION");
				e.printStackTrace();
			} catch (Exception e){
				System.out.println("EXCEPTION: " + e.getMessage());
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			System.out.println("RESULT OF HTTP REQUEST IS: ");
			if ( result != null ){
				System.out.println(result.toString());
			}
			else{
				System.out.println("WAS NULL");
			}
		}
	}
	
	private static JSONObject doHttpRequest(String method, String url, JSONObject headers, JSONObject payload){
		HttpRequestExecutor executor = (HttpRequestExecutor) new HttpRequestExecutor();
		executor.execute(method, url,
				headers != null ? headers.toString() : null,
				payload != null ? payload.toString() : null);

		try {
			return executor.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONObject doGETHttpRequest(String url, JSONObject headers){
		return doHttpRequest("GET", url, headers, null);
	}
	
	public static JSONObject doPOSTHttpRequest(String url, JSONObject headers){
		return doHttpRequest("POST", url, headers, null);
	}

	public static JSONObject doPOSTHttpRequest(String url, JSONObject headers, JSONObject payload){
		return doHttpRequest("POST", url, headers, payload);
	}
	
//	private void enableCookieManagement(){
////		CookieManager cookieManager = CookieManager.getInstance();
//		CookieHandler.setDefault(CookieManager);
//	}
}