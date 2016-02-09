package com.chalkboard.teacher;

import java.io.Serializable;

public class  JobObject implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String jobName;
	private String jobLocation;
	private String jobImage;
	private String jobDate;
	private boolean isJobFavorite;
	
	private boolean isJobMatch;
	
	private boolean isSelected;;
	private boolean is_draft;
	private String jobSalary;
	private String jobDescription;
	
	private String jobPhoto;
	
	private String jobRecruiterName;
	
	private String jobRecruiterAbout;

	
	private String jobRecruiterId;
	
	private String jobMatchDate;

	
	private String jobStatus;
	
	private String jobTitle;
	private String jobCity;
	private String jobCountry;
	private String jobStartdate;
	private String jobIsfavourate;
	private String jobIsmatch;

	
	
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	public String getJobRecruiterId() {
		return jobRecruiterId;
	}
	public void setJobRecruiterId(String jobRecruiterId) {
		this.jobRecruiterId = jobRecruiterId;
	}
	public String getJobMatchDate() {
		return jobMatchDate;
	}
	public void setJobMatchDate(String jobMatchDate) {
		this.jobMatchDate = jobMatchDate;
	}
	public boolean isJobMatch() {
		return isJobMatch;
	}
	public void setJobMatch(boolean isJobMatch) {
		this.isJobMatch = isJobMatch;
	}
	public String getJobSalary() {
		return jobSalary;
	}
	public void setJobSalary(String jobSalary) {
		this.jobSalary = jobSalary;
	}
	public String getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	public String getJobPhoto() {
		return jobPhoto;
	}
	public void setJobPhoto(String jobPhoto) {
		this.jobPhoto = jobPhoto;
	}
	public String getJobRecruiterName() {
		return jobRecruiterName;
	}
	public void setJobRecruiterName(String jobRecruiterName) {
		this.jobRecruiterName = jobRecruiterName;
	}
	public String getJobImage() {
		return jobImage;
	}
	public void setJobImage(String jobImage) {
		this.jobImage = jobImage;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobLocation() {
		return jobLocation;
	}
	public void setJobLocation(String jobLocation) {
		this.jobLocation = jobLocation;
	}
	public String getJobDate() {
		return jobDate;
	}
	public void setJobDate(String jobDate) {
		this.jobDate = jobDate;
	}
	public boolean isJobFavorite() {
		return isJobFavorite;
	}
	public void setJobFavorite(boolean isJobFavorite) {
		this.isJobFavorite = isJobFavorite;
	}
	public String getJobRecruiterAbout() {
		return jobRecruiterAbout;
	}
	public void setJobRecruiterAbout(String jobRecruiterAbout) {
		this.jobRecruiterAbout = jobRecruiterAbout;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public boolean isIs_draft() {
		return is_draft;
	}
	public void setIs_draft(boolean is_draft) {
		this.is_draft = is_draft;
	}
	public String getJobIsmatch() {
		return jobIsmatch;
	}
	public void setJobIsmatch(String jobIsmatch) {
		this.jobIsmatch = jobIsmatch;
	}
	public String getJobIsfavourate() {
		return jobIsfavourate;
	}
	public void setJobIsfavourate(String jobIsfavourate) {
		this.jobIsfavourate = jobIsfavourate;
	}
	public String getJobStartdate() {
		return jobStartdate;
	}
	public void setJobStartdate(String jobStartdate) {
		this.jobStartdate = jobStartdate;
	}
	public String getJobCountry() {
		return jobCountry;
	}
	public void setJobCountry(String jobCountry) {
		this.jobCountry = jobCountry;
	}
	public String getJobCity() {
		return jobCity;
	}
	public void setJobCity(String jobCity) {
		this.jobCity = jobCity;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	
}