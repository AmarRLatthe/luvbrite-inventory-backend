package com.luvbrite.externalservice;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

public class FindDistance {
	private String destination = "";
	private GeoApiContext context;

	public FindDistance(String destination) {
		this.destination = destination;

		context = new GeoApiContext()
				.setApiKey("AIzaSyAkFB_q0sw9ruZsOnY9-TF0cuMfMLG4DI0");
	}

	public double getDistanceInMiles() throws Exception{

		double miles = 0;

		DistanceMatrix matrix = DistanceMatrixApi.newRequest(context)
				.origins(new LatLng(34.040135, -118.439910)) //2126 Cotner Ave
				.destinations(destination, destination, destination)
				.mode(TravelMode.DRIVING)
				.units(Unit.IMPERIAL)
				.await();

		if((matrix.rows.length > 0) &&
				(matrix.rows[0].elements.length>0) &&
				(matrix.rows[0].elements[0].distance != null)){

			long distInMtrs = matrix.rows[0].elements[0].distance.inMeters;
			miles = distInMtrs * (0.625/1000);
		}

		return miles;
	}

	public double getDistanceInMiles(String origin, String destination) throws Exception{

		double miles = 0;

		DistanceMatrix matrix = DistanceMatrixApi.newRequest(context)
				.origins(origin)
				.destinations(destination)
				.mode(TravelMode.DRIVING)
				.units(Unit.IMPERIAL)
				.await();

		if((matrix.rows.length > 0) &&
				(matrix.rows[0].elements.length>0) &&
				(matrix.rows[0].elements[0].distance != null)){

			long distInMtrs = matrix.rows[0].elements[0].distance.inMeters;
			miles = distInMtrs * (0.625/1000);
		}

		return miles;
	}

	public LatLng getLatLng() throws Exception{

		double lat = 0d;
		double lng = 0d;

		GeocodingResult[] results = GeocodingApi.newRequest(context)
				.address(destination).await();

		if((results != null) &&
				(results[0].geometry != null) &&
				(results[0].geometry.location != null)){

			lat = results[0].geometry.location.lat;
			lng = results[0].geometry.location.lng;
		}

		return new LatLng(lat, lng);
	}
}
