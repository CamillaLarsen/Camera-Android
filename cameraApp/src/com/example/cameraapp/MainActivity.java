package com.example.cameraapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	int REQUEST_CODE = 1;
	final static int MEDIA_TYPE_IMAGE = 1;

	Intent intent;
	ImageView imageView;
	Button btnCamera;
	LocationManager locationManager;
	String provider;
	Location location;
	TextView locationView;
	LocationListener locationListener;
	Criteria criteria;
	Bitmap bitmap;
	double lon;
	double lat;
	String lngTxt;
	String latTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.imageView);
		btnCamera = (Button) findViewById(R.id.btnCamera);
		locationView = (TextView) findViewById(R.id.locationView);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(provider);

		locationListener = new LocationListenerClass();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);
		if (savedInstanceState != null) {
			bitmap = savedInstanceState.getParcelable("bitmap");
			imageView.setImageBitmap(bitmap);
		}

		btnCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Beder om tilladelse til at tage et billede.
				intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// Sender data tilbage til MainActivity.
				startActivityForResult(intent, REQUEST_CODE);

			}

		});
	}

	public void onDestroy() {
		locationManager.removeUpdates(locationListener);
		super.onDestroy();
	}

	@Override
	public void onResume() {

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Hvis resultatet er OK.
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				try {
					imageView.setVisibility(View.VISIBLE);
					// Gør billedet mindre og spare plads.
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 8;

					// Bitmap der opbevarer billedet.
					bitmap = (Bitmap) data.getExtras().get("data");

					// Sætter billedet i imageView.
					imageView.setImageBitmap(bitmap);

					// Sætter lokationen
					location.getLongitude();
					location.getLatitude();
					lon = location.getLongitude();
					lngTxt = String.valueOf(lon);
					lat = location.getLatitude();
					latTxt = String.valueOf(lat);
					locationView.setText("Longitude: " + lngTxt + " "
							+ "Latitude: " + latTxt);

				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * Laver en fil
	 */
	@SuppressWarnings("unused")
	private static File saveImage(int type) {
		// Ekstern fil lokation
		File file = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		// Navngiver filen
		String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File imageFile;
		if (type == MEDIA_TYPE_IMAGE) {
			// Laver filen og gemmer i mappen
			imageFile = new File(file.getPath() + File.separator + "IMG_"
					+ fileName + ".jpg");

		} else {
			return null;
		}
		return imageFile;

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putParcelable("bitmap", bitmap);
	}

	// En indre klasse til lokation
	private class LocationListenerClass implements LocationListener {
		@Override
		// Når lokationen forandres
		public void onLocationChanged(Location location) {
			if (location != null) {
				Log.d("LOCATION CHANGED", location.getLatitude() + " ");
				Log.d("LOCATION CHANGED", location.getLongitude() + " ");
			}
			locationManager.removeUpdates(this);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

	}



}
