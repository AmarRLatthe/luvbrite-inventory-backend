package com.luvbrite.model.tookan;

import java.util.ArrayList;
import java.util.List;

public class TookanActivityTimelineResponseData {

	private double end_lat;
	private double end_long;
	private double distance;
	private double seconds;
	private String start_time;
	private String end_time;
	private long start_time_stamp;
	private long end_time_stamp;
	private int stop_or_move_flag;
	
	private List<TookanActivityTimelineResponseData_Tasks> tasks ;

	public double getEnd_lat() {
		return end_lat;
	}

	public void setEnd_lat(int end_lat) {
		this.end_lat = end_lat;
	}

	public double getEnd_long() {
		return end_long;
	}

	public void setEnd_long(int end_long) {
		this.end_long = end_long;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(long distance) {
		this.distance = distance;
	}

	public double getSeconds() {
		return seconds;
	}

	public void setSeconds(long seconds) {
		this.seconds = seconds;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public long getStart_time_stamp() {
		return start_time_stamp;
	}

	public void setStart_time_stamp(long start_time_stamp) {
		this.start_time_stamp = start_time_stamp;
	}

	public long getEnd_time_stamp() {
		return end_time_stamp;
	}

	public void setEnd_time_stamp(long end_time_stamp) {
		this.end_time_stamp = end_time_stamp;
	}

	public int getStop_or_move_flag() {
		return stop_or_move_flag;
	}

	public void setStop_or_move_flag(int stop_or_move_flag) {
		this.stop_or_move_flag = stop_or_move_flag;
	}

	public List<TookanActivityTimelineResponseData_Tasks> getTasks() {
		return tasks;
	}

	public void setTasks(List<TookanActivityTimelineResponseData_Tasks> tasks) {
		this.tasks = tasks;
	}
	
	
}
