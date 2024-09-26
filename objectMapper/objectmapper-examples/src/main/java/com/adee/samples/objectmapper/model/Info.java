package com.adee.samples.objectmapper.model;

import java.util.Date;

public class Info {

	private Country country;
	private Date now;

	public Info(Country country, Date now) {
		this.country = country;
		this.now = now;
	}

	public Info() {

	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}

    public static void main(String[] args) {
        Country country = new Country("India", 135260000000L, 29, true);
        Date date = new Date();
        Info info = new Info(country, date);

        System.out.println("Country: " + info.getCountry());
        System.out.println("Date: " + info.getNow());
    }

}
