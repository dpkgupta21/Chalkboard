package com.chalkboard.model;

import java.io.Serializable;

/**
 * Created by DeepakGupta on 2/20/16.
 */
public class RecruiterDTO implements Serializable {
    public String name;
    public String about;
    public String logo;
    public String school_photo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSchool_photo() {
        return school_photo;
    }

    public void setSchool_photo(String school_photo) {
        this.school_photo = school_photo;
    }
}
