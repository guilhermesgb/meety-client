package meety.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import meety.client.http.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MeetySession extends Activity {

	public static final int REQUEST_CODE = 102;
	public static final int RESULT_CODE_OK = 1020;
	public static boolean MEETY_SESSION_IN_PROGRESS = true;
	
	private static boolean doTrackFriendHTTPRequest(Context context, final MeetySession activity){

		if ( currentPositionMarker == null || friendPositionMarker == null ){
			return false;
		}
		
		System.out.println("Tracking friend...");
		
		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("Host", "meety-server.herokuapp.com");
		pairs.put("Content-Type", "application/json");
		pairs.put("Accept", "application/json");
		JSONObject headers = new JSONObject(pairs);

		pairs.clear();
		LatLng latLng = currentPositionMarker.getPosition();
		pairs.put("position", latLng.latitude+", "+latLng.longitude);
		JSONObject payload = new JSONObject(pairs);
		
		JSONObject response = HttpUtils.doPOSTHttpRequest("http://meety-server.herokuapp.com/session",
				headers, payload);

		if ( response == null ){
			return false;
		} else
			try {
				Integer responseCode = (Integer) response.get("code");
				String responseBody = (String) response.get("body");
				
				if ( responseCode == 200 ){
					JSONArray tracked = new JSONArray(responseBody);
					
					String username = null, position = null;
					for ( int i=0; i<tracked.length(); i++ ){
						JSONObject users = tracked.getJSONObject(i);
						Iterator<String> usernames = users.keys();
						while ( usernames.hasNext() ){
							username = usernames.next();
							position = users.getString(username);
						}
					}
					if ( username != null && position != null ){

						double latitude = Double.valueOf(position.split(",")[0].trim());
						double longitude = Double.valueOf(position.split(",")[1].trim());
						
						friendPos = new LatLng(latitude, longitude);

						if ( friendPos != null ){
							
							friendPositionMarker = gMap.addMarker(new MarkerOptions().position(friendPos)
									.title("Friend").icon(BitmapDescriptorFactory.fromResource(R.drawable.bluep)));
							friendPositionMarker.setPosition(friendPos);
						}
					}
				}
				else{
					MeetySession.MEETY_SESSION_IN_PROGRESS = false;
					activity.handler.postDelayed(new Runnable() {
						public void run() {
							activity.setResult(RESULT_CODE_OK);
							activity.finish();
						}
					}, 5000);
					return true;
				}
			} catch (Exception e) {
				System.out.println("doTrackFriendHTTPRequest EXCEPTION");
				CharSequence toastText = "doTrackFriendHTTPRequest EXCEPTION";
				Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
				toast.show();
				e.printStackTrace();
			}
			return false;
	}
	
	static class TrackFriend implements Runnable{

		
		private final Handler handler;
		private final Context context;
		private final MeetySession activity;
		public TrackFriend(Handler handler, Context context, MeetySession activity){
			this.handler = handler;
			this.context = context;
			this.activity = activity;
		}
		
		@Override
		public void run() {
			if ( MeetySession.doTrackFriendHTTPRequest(context, activity) ){
				CharSequence toastText = "Meety session has come to an end...";
				Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
				toast.show();
			}
			if ( MeetySession.MEETY_SESSION_IN_PROGRESS ){
				handler.postDelayed(this, 5000);
			}
		}
		
	}

	private static GoogleMap gMap;
	private CameraPosition cameraPosition;
	private static Marker currentPositionMarker = null;
	private static Marker friendPositionMarker = null;
	public Handler handler;
	static LatLng friendPos = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meety_session);
		gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.meety_session_googlemap)).getMap();
		gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		gMap.setBuildingsEnabled(true);
		
		MeetySession.MEETY_SESSION_IN_PROGRESS = true;
		
		handler = new Handler();
		handler.post(new TrackFriend(handler, getApplicationContext(), this));
		
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		String locationProvider = LocationManager.GPS_PROVIDER;

		LocationListener locationListener = new LocationListener() {

			public void onLocationChanged(Location myLocation) {
				makeUseOfNewLocation(myLocation);
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};
		locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
	}

	private void makeUseOfNewLocation(Location location) {

		gMap.clear();

		LatLng newpos = new LatLng(location.getLatitude(),
				location.getLongitude());

		cameraPosition = new CameraPosition.Builder().target(newpos).zoom(17)
				.bearing(0).tilt(30).build();

		gMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));

		currentPositionMarker = gMap.addMarker(new MarkerOptions()
				.position(newpos).title("You")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.redp)));
		currentPositionMarker.setPosition(newpos);

	}

}