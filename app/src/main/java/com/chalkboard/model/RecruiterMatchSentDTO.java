package com.chalkboard.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DeepakGupta on 2/19/16.
 */
public class RecruiterMatchSentDTO implements Serializable{

    public String id;
    public String name;
    public String email;
    public String city;
    public String country;
    public String about;
    public String age;
    public String gender;
    public List<TeacherExperienceDTO> TeacherEducationA;
    public List<TeacherExperienceDTO> TeacherExperienceA;
    public String image;
    public boolean is_favorite;
    public boolean is_match;


    public List<TeacherExperienceDTO> getTeacherEducationA() {
        return TeacherEducationA;
    }

    public void setTeacherEducationA(List<TeacherExperienceDTO> teacherEducationA) {
        TeacherEducationA = teacherEducationA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<TeacherExperienceDTO> getTeacherExperienceA() {
        return TeacherExperienceA;
    }

    public void setTeacherExperienceA(List<TeacherExperienceDTO> teacherExperienceA) {
        TeacherExperienceA = teacherExperienceA;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}
