package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.stclaircollege.fitgrind.api.Food;
import ca.stclaircollege.fitgrind.api.Nutrient;
import ca.stclaircollege.fitgrind.database.DatabaseHandler;


public class AddCustomFoodFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private EditText name, serving, calories, sugar, totalFat, carbohydrate, saturatedFat, transFat, cholesterol,
                     sodium, fiber, protein, vitaminA, vitaminC, calcium, iron, potassium;
    private Button submitButton;

    public AddCustomFoodFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_custom_food, container, false);


        name = (EditText) view.findViewById(R.id.food_name_field);
        serving = (EditText) view.findViewById(R.id.serving_size_field);
        calories = (EditText) view.findViewById(R.id.calories_field);
        sugar = (EditText) view.findViewById(R.id.sugar_field);
        totalFat = (EditText) view.findViewById(R.id.totalfat_field);
        carbohydrate = (EditText) view.findViewById(R.id.carbohydrate_field);
        saturatedFat = (EditText) view.findViewById(R.id.saturatedfat_field);
        transFat = (EditText) view.findViewById(R.id.transfat_field);
        cholesterol = (EditText) view.findViewById(R.id.cholesterol_field);
        sodium = (EditText) view.findViewById(R.id.sodium_field);
        fiber = (EditText) view.findViewById(R.id.fiber_field);
        protein = (EditText) view.findViewById(R.id.protein_field);
        vitaminA = (EditText) view.findViewById(R.id.vitamina_field);
        vitaminC = (EditText) view.findViewById(R.id.vitaminc_field);
        calcium = (EditText) view.findViewById(R.id.calcium_field);
        iron = (EditText) view.findViewById(R.id.iron_field);
        potassium = (EditText) view.findViewById(R.id.potassium_field);


        submitButton = (Button) view.findViewById(R.id.submitButton);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFieldFilled()) {

                    Food food = new Food(name.getText().toString(), serving.getText().toString());

                    food.addNutrient(new Nutrient("calories", Double.parseDouble(calories.getText().toString())));
                    food.addNutrient(new Nutrient("sugar", Double.parseDouble(sugar.getText().toString())));
                    food.addNutrient(new Nutrient("total_fat", Double.parseDouble(totalFat.getText().toString())));
                    food.addNutrient(new Nutrient("carbohydrate", Double.parseDouble(carbohydrate.getText().toString())));
                    food.addNutrient(new Nutrient("saturated_fat", Double.parseDouble(saturatedFat.getText().toString())));
                    food.addNutrient(new Nutrient("trans_fat", Double.parseDouble(transFat.getText().toString())));
                    food.addNutrient(new Nutrient("cholesterol", Double.parseDouble(cholesterol.getText().toString())));
                    food.addNutrient(new Nutrient("sodium", Double.parseDouble(sodium.getText().toString())));
                    food.addNutrient(new Nutrient("fiber", Double.parseDouble(fiber.getText().toString())));
                    food.addNutrient(new Nutrient("protein", Double.parseDouble(protein.getText().toString())));
                    food.addNutrient(new Nutrient("vitamin_a", Double.parseDouble(vitaminA.getText().toString())));
                    food.addNutrient(new Nutrient("vitamin_c", Double.parseDouble(vitaminC.getText().toString())));
                    food.addNutrient(new Nutrient("calcium", Double.parseDouble(calcium.getText().toString())));
                    food.addNutrient(new Nutrient("iron", Double.parseDouble(iron.getText().toString())));
                    food.addNutrient(new Nutrient("potassium", Double.parseDouble(potassium.getText().toString())));
                    DatabaseHandler db = new DatabaseHandler(getContext());
                    long id = db.insertCustomFood(food);
                    if (id != -1 && db.insertFoodLog(id) != -1) {

                        clearText();
                        Toast.makeText(getActivity(), R.string.db_insert_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), R.string.db_error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.invalid_field, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


    public boolean isFieldFilled() {
        return !isEmpty(name) && !isEmpty(serving) && !isEmpty(calories) && !isEmpty(sugar) && !isEmpty(totalFat) && !isEmpty(carbohydrate) &&
            !isEmpty(saturatedFat) && !isEmpty(transFat) && !isEmpty(cholesterol) && !isEmpty(sodium) && !isEmpty(fiber) && !isEmpty(protein) &&
            !isEmpty(vitaminA) && !isEmpty(vitaminC) && !isEmpty(calcium) && !isEmpty(iron) && !isEmpty(potassium);
    }

    private boolean isEmpty(EditText e) {
        return e.getText().toString().trim().length() == 0;
    }


    public void clearText() {
        name.setText("");
        serving.setText("");
        calories.setText("");
        sugar.setText("");
        totalFat.setText("");
        carbohydrate.setText("");
        sodium.setText("");
        fiber.setText("");
        protein.setText("");
        vitaminA.setText("");
        vitaminC.setText("");
        calcium.setText("");
        iron.setText("");
        potassium.setText("");
        saturatedFat.setText("");
        transFat.setText("");
        cholesterol.setText("");
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
