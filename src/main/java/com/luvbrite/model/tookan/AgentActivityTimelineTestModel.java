package com.luvbrite.model.tookan;

public class AgentActivityTimelineTestModel {

	
	String drivername;
	Long fleet_id;
	double distancetravelled;
	String distnceInMiles =""; 
	public String getDrivername() {
		return drivername;
	}
	public void setDrivername(String drivername) {
		this.drivername = drivername;
	}
	public Long getFleet_id() {
		return fleet_id;
	}
	public void setFleet_id(Long fleet_id) {
		this.fleet_id = fleet_id;
	}
	public Double getDistancetravelled() {
		return distancetravelled;
	}
	public void setDistancetravelled(Double distancetravelled) {
		this.distancetravelled = distancetravelled;
	}
	@Override
	public String toString() {
		return "AgentActivityTimelineTestModel [\ndrivername=" + drivername + ",\nfleet_id=" + fleet_id
				+ ", \ndistancetravelled=" + distancetravelled + "]";
	}

	
	
	
	
	
}
