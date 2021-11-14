package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class WeightCalculator {

    private static double CONVERT_TO_KG = 0.453592;
    private static double CONVERT_TO_CM = 2.54;
    private String gender;
    private double age, height, weight, weightgoal, lifestyle;
    private double deficit;
    private double BMR;

    public WeightCalculator(Context context) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        gender = SP.getString("gender_keys", "Male");
        age = Double.parseDouble(SP.getString("age", "0"));
        height = Double.parseDouble(SP.getString("height_inches", "0")) * CONVERT_TO_CM;
        weight = Double.parseDouble(SP.getString("weight", "0")) * CONVERT_TO_KG;
        weightgoal = Double.parseDouble(SP.getString("weight_goal", "0")) * CONVERT_TO_KG;
        lifestyle = Double.parseDouble(SP.getString("lifestyle_key", "1.2"));
        deficit = Double.parseDouble(SP.getString("weight_per_week", "500"));

        if (gender.equals("Male")) {
            BMR = (10 * weight + 6.25 * height - 5 * age + 5) * lifestyle;
        } else {
            BMR = (10 * weight + 6.25 * height - 5 * age - 161) * lifestyle;
        }

        BMR = (weightgoal > weight) ? BMR + deficit : BMR - deficit;
    }

    public WeightCalculator(SharedPreferences SP) {
        gender = SP.getString("gender_keys", "Male");
        age = Double.parseDouble(SP.getString("age", "0"));
        height = Double.parseDouble(SP.getString("height_inches", "0")) * CONVERT_TO_CM;
        weight = Double.parseDouble(SP.getString("weight", "0")) * CONVERT_TO_KG;
        weightgoal = Double.parseDouble(SP.getString("weight_goal", "0")) * CONVERT_TO_KG;
        lifestyle = Double.parseDouble(SP.getString("lifestyle_key", "1.2"));
        deficit = Double.parseDouble(SP.getString("weight_per_week", "500"));

        if (gender.equals("Male")) {
            BMR = (10 * weight + 6.25 * height - 5 * age + 5) * lifestyle;
        } else {
            BMR = (10 * weight + 6.25 * height - 5 * age - 161) * lifestyle;
        }

        BMR = (weightgoal > weight) ? BMR + deficit : BMR - deficit;
    }

    public double getBMR() {
        return Math.floor(BMR);
    }

    public String getCurrentWeight() {
        return String.format("%.0f lbs",(weight / CONVERT_TO_KG));
    }

    public String getCalorieGoal() {
        return String.format("%.0f", Math.floor(this.BMR));
    }

    public String getWeightGoal() {
        return String.format("%.0f lbs", (weightgoal / CONVERT_TO_KG));
    }
}
