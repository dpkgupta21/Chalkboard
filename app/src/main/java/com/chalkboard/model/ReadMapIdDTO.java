package com.chalkboard.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by DeepakGupta on 2/20/16.
 */
public class ReadMapIdDTO implements Serializable {

    public Map<String, Boolean> recruiterMapId;
    public Map<String, Boolean> teacherMapId;

    public Map<String, Boolean> getRecruiterMapId() {
        return recruiterMapId;
    }

    public void setRecruiterMapId(Map<String, Boolean> recruiterMapId) {
        this.recruiterMapId = recruiterMapId;
    }

    public Map<String, Boolean> getTeacherMapId() {
        return teacherMapId;
    }

    public void setTeacherMapId(Map<String, Boolean> teacherMapId) {
        this.teacherMapId = teacherMapId;
    }
}
