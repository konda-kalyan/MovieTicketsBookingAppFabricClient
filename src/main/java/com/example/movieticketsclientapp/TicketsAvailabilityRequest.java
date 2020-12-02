package com.example.movieticketsclientapp;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class TicketsAvailabilityRequest {
	
	@NotNull(message="theatre is mandatory")
	private String theatre;
	
	@NotNull(message="screen is mandatory")
	private String screen;
	
	@NotNull(message="show is mandatory")
	private String show;
	
	@NotNull(message="show is mandatory")
	private Date showDate;

	public TicketsAvailabilityRequest() {
	}

	public String getTheatre() {
		return theatre;
	}

	public void setTheatre(String theatre) {
		this.theatre = theatre;
	}

	public String getScreen() {
		return screen;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}
		
	public Date getShowDate() {
		return showDate;
	}

	public void setShowDate(Date showDate) {
		this.showDate = showDate;
	}
}
