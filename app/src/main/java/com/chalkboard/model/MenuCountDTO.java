package com.chalkboard.model;

import java.io.Serializable;

/**
 * Created by DeepakGupta on 2/19/16.
 */
public class MenuCountDTO implements Serializable {

    public int credits;
    public int matchRequestCount;
    public int matchCount;
    public int notification;
    public int msgcount;
    public String message;
    public boolean status;

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getMatchRequestCount() {
        return matchRequestCount;
    }

    public void setMatchRequestCount(int matchRequestCount) {
        this.matchRequestCount = matchRequestCount;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public int getMsgcount() {
        return msgcount;
    }

    public void setMsgcount(int msgcount) {
        this.msgcount = msgcount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
