package ca.stclaircollege.fitgrind.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Nutrient implements Parcelable {
    public static final String ID_KEY = "nutrient_id";
    public static final String NUTRIENT_KEY = "nutrient";
    public static final String UNIT_KEY = "unit";
    public static final String VALUE_KEY = "value";

    private int nutrientId;
    private String nutrient;
    private String unit = "g"; // default
    private double value;

    public Nutrient(String nutrient, String unit, String value) {
        this.nutrient = (nutrient.equals("Energy")) ? "Calories" : nutrient;
        this.unit = unit;
        this.value = (value.equals("--")) ? 0 : Double.parseDouble(value);
    }

    public Nutrient(String nutrient, double value) {
        this.nutrient = nutrient;
        this.value = value;
        switch (nutrient) {
            case "Vitamin A, RAE":
                this.unit =  "\u03bcg";
                break;
            case "Calcium, Ca":
            case "Vitamin C, total ascorbic acid":
            case "Iron, Fe":
            case "Cholesterol":
            case "Potassium, K":
            case "Sodium, Na":
                this.unit = "mg";
                break;
            case "Calories":
                this.unit = "kcal";
                break;
        }
    }

    protected Nutrient(Parcel in) {
        nutrientId = in.readInt();
        nutrient = in.readString();
        unit = in.readString();
        value = in.readDouble();
    }

    public String getNutrient() {
        return nutrient;
    }

    public String getUnit() {
        return unit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(nutrientId);
        dest.writeString(nutrient);
        dest.writeString(unit);
        dest.writeDouble(value);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Nutrient> CREATOR = new Parcelable.Creator<Nutrient>() {
        @Override
        public Nutrient createFromParcel(Parcel in) {
            return new Nutrient(in);
        }

        @Override
        public Nutrient[] newArray(int size) {
            return new Nutrient[size];
        }
    };
}
