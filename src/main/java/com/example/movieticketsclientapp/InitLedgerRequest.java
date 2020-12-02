package com.example.movieticketsclientapp;

import org.springframework.beans.factory.annotation.Value;

// No field is mandatory here. If particular field's info is not passed then we set with default values, which are mentioned in problem statement
public class InitLedgerRequest {

	@Value("${theatres.count:3}")
	private Integer theatresCount;
	
	@Value("${theatre.sceens.count:5}")
	private Integer screensCount;
	
	@Value("${daily.shows.count:4}")
	private Integer showsCount;
	
	@Value("${max.tickets.per.show:100}")
	private Integer maxTicketPerShow;
	
	@Value("${max.sodas.available:200}")
	private Integer maxSodasAvailable;

	public InitLedgerRequest() {
	}

	public Integer getTheatresCount() {
		return theatresCount;
	}

	public void setTheatresCount(Integer theatresCount) {
		this.theatresCount = theatresCount;
	}

	public Integer getScreensCount() {
		return screensCount;
	}

	public void setScreensCount(Integer screensCount) {
		this.screensCount = screensCount;
	}

	public Integer getShowsCount() {
		return showsCount;
	}

	public void setShowsCount(Integer showsCount) {
		this.showsCount = showsCount;
	}

	public Integer getMaxTicketPerShow() {
		return maxTicketPerShow;
	}

	public void setMaxTicketPerShow(Integer maxTicketPerShow) {
		this.maxTicketPerShow = maxTicketPerShow;
	}

	public Integer getMaxSodasAvailable() {
		return maxSodasAvailable;
	}

	public void setMaxSodasAvailable(Integer maxSodasAvailable) {
		this.maxSodasAvailable = maxSodasAvailable;
	}
}
