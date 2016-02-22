package com.chalkboard.model;

import java.io.Serializable;

/**
 * Created by DeepakGupta on 2/19/16.
 */
public class MatchSentDTO implements Serializable{

    public String id;
    public String title;
    public String city;
    public String country;
    public String image;
    public String salary;
    public String start_date;
    public String description;
    public String match_date;
    public boolean is_favorite;
    public boolean is_match;
    public String recruiter_id;

    public String getRecruiter_id() {
        return recruiter_id;
    }

    public void setRecruiter_id(String recruiter_id) {
        this.recruiter_id = recruiter_id;
    }

    public RecruiterDTO Recruiter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMatch_date() {
        return match_date;
    }

    public void setMatch_date(String match_date) {
        this.match_date = match_date;
    }

    public boolean is_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    public boolean is_match() {
        return is_match;
    }

    public void setIs_match(boolean is_match) {
        this.is_match = is_match;
    }

    public RecruiterDTO getRecruiter() {
        return Recruiter;
    }

    public void setRecruiter(RecruiterDTO recruiter) {
        Recruiter = recruiter;
    }
}
