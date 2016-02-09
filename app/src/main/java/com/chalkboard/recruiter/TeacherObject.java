package com.chalkboard.recruiter;

import java.io.Serializable;

public class  TeacherObject implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String teacherName;
	private String teacherLocation;
	private String teacherImage;
	private String teacherAge;
	private boolean isTeacherFavorite;
	
	private String teacherEmail;
	
	private String teacherAbout;
	
	private String teacherGender;
	
	private String teacherEducation;
	
	private String teacherExperience;
	
	private boolean isTeacherMatch;
	
	
	public String getTeacherEmail() {
		return teacherEmail;
	}
	public void setTeacherEmail(String teacherEmail) {
		this.teacherEmail = teacherEmail;
	}
	public String getTeacherAbout() {
		return teacherAbout;
	}
	public void setTeacherAbout(String teacherAbout) {
		this.teacherAbout = teacherAbout;
	}
	public String getTeacherGender() {
		return teacherGender;
	}
	public void setTeacherGender(String teacherGender) {
		this.teacherGender = teacherGender;
	}
	public String getTeacherEducation() {
		return teacherEducation;
	}
	public void setTeacherEducation(String teacherEducation) {
		this.teacherEducation = teacherEducation;
	}
	public String getTeacherExperience() {
		return teacherExperience;
	}
	public void setTeacherExperience(String teacherExperience) {
		this.teacherExperience = teacherExperience;
	}
	public boolean isTeacherMatch() {
		return isTeacherMatch;
	}
	public void setTeacherMatch(boolean isTeacherMatch) {
		this.isTeacherMatch = isTeacherMatch;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public String getTeacherLocation() {
		return teacherLocation;
	}
	public void setTeacherLocation(String teacherLocation) {
		this.teacherLocation = teacherLocation;
	}
	public String getTeacherImage() {
		return teacherImage;
	}
	public void setTeacherImage(String teacherImage) {
		this.teacherImage = teacherImage;
	}
	public String getTeacherAge() {
		return teacherAge;
	}
	public void setTeacherAge(String teacherAge) {
		this.teacherAge = teacherAge;
	}
	public boolean isTeacherFavorite() {
		return isTeacherFavorite;
	}
	public void setTeacherFavorite(boolean isTeacherFavorite) {
		this.isTeacherFavorite = isTeacherFavorite;
	}
	
	
	

}