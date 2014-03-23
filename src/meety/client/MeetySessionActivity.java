package meety.client;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MeetySessionActivity extends Activity {
	
	private GoogleMap gMap;
	private CameraPosition cameraPosition;
	private Marker currentPosition = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meety_session);
		startMap();
		
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new LocationListener() {
		
		
			
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		    	
		      makeUseOfNewLocation(location);
		    }

			public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		  String locationProvider = LocationManager.GPS_PROVIDER;
		  locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
	}
	
    private void makeUseOfNewLocation(Location location) {
    	
    	LatLng newpos = new LatLng(location.getLatitude(), location.getLongitude());
    	
		cameraPosition = new CameraPosition.Builder()
		.target(newpos)                                 
	    .zoom(19)                   
	    .bearing(0)                
	    .tilt(30)                   
	    .build();
		gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		if ( currentPosition == null ){
			currentPosition = gMap.addMarker(new MarkerOptions()
			.position(newpos)
			.title("You")
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		}
		currentPosition.setPosition(newpos);
	}

	private void startMap() {
		
		gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.id_googlemap)).getMap();
		gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		gMap.setBuildingsEnabled(true);

	}

}