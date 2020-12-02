package com.example.movieticketsclientapp;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class TicketsRequest {
	
	@NotNull(message="theatre is mandatory")
	private String theatre;
	
	@NotNull(message="screen is mandatory")
	private String screen;
	
	@NotNull(message="show is mandatory")
	private String show;
	
	@NotNull(message="buyerName is mandatory")
	private String buyerName;
	
	@NotNull(message="numberOfTicketsBuying is mandatory")
	private Integer numberOfTicketsBuying;
	
	private Date showDate;	// optional: default is today

	public TicketsRequest() {
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

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public Integer getNumberOfTicketsBuying() {
		return numberOfTicketsBuying;
	}

	public void setNumberOfTicketsBuying(Integer numberOfTicketsBuying) {
		this.numberOfTicketsBuying = numberOfTicketsBuying;
	}
	
	public Date getShowDate() {
		return showDate;
	}

	public void setShowDate(Date showDate) {
		this.showDate = showDate;
	}
}
