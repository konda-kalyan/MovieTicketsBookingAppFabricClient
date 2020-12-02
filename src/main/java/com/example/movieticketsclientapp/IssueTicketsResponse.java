package com.example.movieticketsclientapp;

public class IssueTicketsResponse {
	
	private String status;
	
	private Integer statusCode;
	
	private String message;
	
	private TicketInfo ticketInfo;
	
	public IssueTicketsResponse() {
		
	}

	public IssueTicketsResponse(String status, Integer statusCode, String message, TicketInfo ticketInfo) {
		super();
		this.status = status;
		this.statusCode = statusCode;
		this.message = message;
		this.ticketInfo = ticketInfo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public TicketInfo getTicketInfo() {
		return ticketInfo;
	}

	public void setTicketInfo(TicketInfo ticketInfo) {
		this.ticketInfo = ticketInfo;
	}
}
