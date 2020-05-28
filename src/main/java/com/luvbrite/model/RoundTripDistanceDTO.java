package com.luvbrite.model;

import com.google.maps.model.LatLng;

public class RoundTripDistanceDTO {

	private LatLng latLng;
	private double roundTripDistance;



	public LatLng getLatLng() {
		return latLng;
	}
	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}
	public double getRoundTripDistance() {
		return roundTripDistance;
	}
	public void setRoundTripDistance(double roundTripDistance) {
		this.roundTripDistance = roundTripDistance;
	}



}
