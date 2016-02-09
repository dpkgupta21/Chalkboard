package com.chalkboard;

public class CountryData {
	
	private String Country_Id;
	private String Country_Name;
	
	private boolean isChecked;
	
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public String getCountry_Id() {
		return Country_Id;
	}
	public void setCountry_Id(String country_Id) {
		Country_Id = country_Id;
	}
	public String getCountry_Name() {
		return Country_Name;
	}
	public void setCountry_Name(String country_Name) {
		Country_Name = country_Name;
	}
	
	

}
