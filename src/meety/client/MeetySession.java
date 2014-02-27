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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * 
 * @author Tales Tenorio de Souza Pimentel - tales.tsp@gmail.com
 *
 */
public class MeetySession extends Activity {
	
	GoogleMap gMap;
	CameraPosition cameraPosition;
	
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
    	
		cameraPosition = new CameraPosition.Builder()
		.target(new LatLng(location.getLatitude(), location.getLongitude()))                                 
	    .zoom(19)                   
	    .bearing(0)                
	    .tilt(30)                   
	    .build(); 
		gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		gMap.setBuildingsEnabled(true);

	}

	private void startMap() {
		
		gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.id_googlemap)).getMap();
		gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		
		cameraPosition = new CameraPosition.Builder()
		.target(new LatLng(-7.2290, -35.9))
	    .zoom(18)                   
	    .bearing(0)                
	    .tilt(30)                   
	    .build(); 
		gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		gMap.setBuildingsEnabled(true);
	
		
	}

}