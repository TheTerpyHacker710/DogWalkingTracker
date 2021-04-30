package uk.ac.abertay.cmp309.dogtracker;

import android.net.Uri;

public class DogProfile {
    String dogName, dogPhotoURL;
    int caloriesPerMeal, dailyCalories, dogAge;
    double hoursTrained, hoursTrainedToday, hoursWalked, hoursWalkedToday;
    Boolean profileSet;

    public String getDogName(){
        return dogName;
    }
    public void setDogName(String name){
        this.dogName = name;
    }

    public int getCaloriesPerMeal() {
        return caloriesPerMeal;
    }
    public void setCaloriesPerMeal(int caloriesPerMeal) {
        this.caloriesPerMeal = caloriesPerMeal;
    }

    public int getDailyCalories() {
        return dailyCalories;
    }
    public void setDailyCalories(int dailyCalories) {
        this.dailyCalories = dailyCalories;
    }

    public int getDogAge() {
        return dogAge;
    }
    public void setDogAge(int dogAge) {
        this.dogAge = dogAge;
    }

    public double getHoursTrained() {
        return  hoursTrained;
    }
    public void setHoursTrained(double hoursTrained) {
        this.hoursTrained = hoursTrained;
    }

    public double getHoursTrainedToday() {
        return hoursTrainedToday;
    }
    public void setHoursTrainedToday(double hoursTrainedToday) { this.hoursTrainedToday = hoursTrainedToday; }

    public double getHoursWalked() {
        return hoursWalked;
    }
    public void setHoursWalked(double hoursWalked) {
        this.hoursWalked = hoursWalked;
    }

    public double getHoursWalkedToday() {
        return hoursWalkedToday;
    }
    public void setHoursWalkedToday(double hoursWalkedToday) { this.hoursWalkedToday = hoursWalkedToday; }

    public Boolean getProfileSet() {
        return profileSet;
    }
    public void setProfileSet(Boolean profileSet) {
        this.profileSet = profileSet;
    }

    public String getDogPhotoURL() {
        return dogPhotoURL;
    }
    public void setDogPhotoURL(String dogPhotoURL) {
        this.dogPhotoURL = dogPhotoURL;
    }
}
