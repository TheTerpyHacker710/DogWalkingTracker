package uk.ac.abertay.cmp309.dogtracker;

import android.net.Uri;

public class DogProfile {
    String dogName, dogPhotoURL;
    int caloriesPerMeal, dailyCalories, dogAge, hoursTrained, hoursTrainedToday, hoursWalked, hoursWalkedToday;
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

    public int getHoursTrained() {
        return  hoursTrained;
    }
    public void setHoursTrained(int hoursTrained) {
        this.hoursTrained = hoursTrained;
    }

    public int getHoursTrainedToday() {
        return hoursTrainedToday;
    }
    public void setHoursTrainedToday(int hoursTrainedToday) {
        this.hoursTrainedToday = hoursTrainedToday;
    }

    public int getHoursWalked() {
        return hoursWalked;
    }
    public void setHoursWalked(int hoursWalked) {
        this.hoursWalked = hoursWalked;
    }

    public int getHoursWalkedToday() {
        return hoursWalkedToday;
    }
    public void setHoursWalkedToday(int hoursWalkedToday) {
        this.hoursWalkedToday = hoursWalkedToday;
    }

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
