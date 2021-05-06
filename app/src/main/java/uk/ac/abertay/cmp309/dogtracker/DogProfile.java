package uk.ac.abertay.cmp309.dogtracker;

//DogProfile Class
//Structures data from database to allow ease of access
public class DogProfile {

    //Declare Variables for DogProfile
    String dogName, dogPhotoURL;
    int caloriesPerMeal, dailyCalories, dogAge;
    double hoursTrained, hoursTrainedToday, hoursWalked, hoursWalkedToday;
    Boolean profileSet;

    //Getters and Setters

    //Get and Set Dog Name
    public String getDogName(){
        return dogName;
    }
    public void setDogName(String name){
        this.dogName = name;
    }

    //Get and Set Calories per Meal
    public int getCaloriesPerMeal() {
        return caloriesPerMeal;
    }
    public void setCaloriesPerMeal(int caloriesPerMeal) {
        this.caloriesPerMeal = caloriesPerMeal;
    }

    //Get and Set Daily Calories
    public int getDailyCalories() {
        return dailyCalories;
    }
    public void setDailyCalories(int dailyCalories) {
        this.dailyCalories = dailyCalories;
    }

    //Get and Set Dog Age
    public int getDogAge() {
        return dogAge;
    }
    public void setDogAge(int dogAge) {
        this.dogAge = dogAge;
    }

    //Get and Set Hours Trained
    public double getHoursTrained() {
        return  hoursTrained;
    }
    public void setHoursTrained(double hoursTrained) {
        this.hoursTrained = hoursTrained;
    }

    //Get and Set Hours Trained Today
    public double getHoursTrainedToday() {
        return hoursTrainedToday;
    }
    public void setHoursTrainedToday(double hoursTrainedToday) { this.hoursTrainedToday = hoursTrainedToday; }

    //Get and Set Hours Walked
    public double getHoursWalked() {
        return hoursWalked;
    }
    public void setHoursWalked(double hoursWalked) {
        this.hoursWalked = hoursWalked;
    }

    //Get and Set Hours Walked Today
    public double getHoursWalkedToday() {
        return hoursWalkedToday;
    }
    public void setHoursWalkedToday(double hoursWalkedToday) { this.hoursWalkedToday = hoursWalkedToday; }

    //Get and Set Profile Set
    public Boolean getProfileSet() {
        return profileSet;
    }
    public void setProfileSet(Boolean profileSet) {
        this.profileSet = profileSet;
    }

    //Get and Set Dog Photo Url
    public String getDogPhotoURL() {
        return dogPhotoURL;
    }
    public void setDogPhotoURL(String dogPhotoURL) {
        this.dogPhotoURL = dogPhotoURL;
    }
}
