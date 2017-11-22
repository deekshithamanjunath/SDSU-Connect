package com.example.deekshithamanjunath.assignment5;

/**
 * Created by deekshithamanjunath on 4/2/17.
 */

public class UserAttributes {

    public double getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public double getUserLatitude() {

        return userLatitude;
    }

    public void setUserLatitude(double userLatitude) {
        this.userLatitude = userLatitude;
    }

    public int getUserYear() {

        return userYear;
    }

    public void setUserYear(int userYear) {
        this.userYear = userYear;
    }

    public String getUserCity() {

        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserState() {

        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getUserCountry() {

        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public String getUserNickname() {

        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    private String userNickname;
    private String userEmail;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    private String userCountry;
    private String userState;
    private String userCity;
    private int userYear;
    private double userLatitude;
    private double userLongitude;
    private int userId;

    public String getUserTimeStamp() {
        return userTimeStamp;
    }

    public void setUserTimeStamp(String userTimeStamp) {
        this.userTimeStamp = userTimeStamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private String userTimeStamp;

}
