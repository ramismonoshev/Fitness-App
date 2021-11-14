package ca.stclaircollege.fitgrind;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.stclaircollege.fitgrind.api.Food;
import ca.stclaircollege.fitgrind.api.Nutrient;
import ca.stclaircollege.fitgrind.database.DatabaseHandler;



public class EditFoodFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private long foodId;
    private Food food;
    private ListView mListView;

    private OnFragmentInteractionListener mListener;

    public EditFoodFragment() {

    }


    public static EditFoodFragment newInstance(long id) {
        EditFoodFragment fragment = new EditFoodFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            foodId = getArguments().getLong(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_food, container, false);
        mListView = (ListView) view.findViewById(R.id.edit_food_listview);

        if (foodId != 0) {
            DatabaseHandler db = new DatabaseHandler(getContext());
            food = db.selectFood(foodId);
            db.close();

            if (food != null) {

                mListView.setAdapter(new CustomAdapter(getContext(), food.getNutrients()));


                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        Nutrient nutrient = (Nutrient) mListView.getItemAtPosition(position);
                        builder.setTitle("Edit " + nutrient.getNutrient() + " Value");


                        final EditText input = new EditText(getContext());


                        input.setText("" + nutrient.getValue());


                        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        builder.setView(input);


                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DatabaseHandler db = new DatabaseHandler(getContext());

                                food.setId(foodId);

                                food.getNutrients().get(position).setValue(Double.parseDouble(input.getText().toString()));

                                if (db.updateFood(food)) {
                                    ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                    Toast.makeText(getContext(), R.string.db_update_success, Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                });
            }
        }

        return view;
    }

    public class CustomAdapter extends ArrayAdapter<Nutrient> {

        public CustomAdapter(Context context, ArrayList<Nutrient> nutrients) {
            super(context, 0, nutrients);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Nutrient nutrient = getItem(position);

            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_edited_food_item, parent, false);
            TextView nutrientName = (TextView) convertView.findViewById(R.id.nutrient_name);
            TextView nutrientValue = (TextView) convertView.findViewById(R.id.nutrient_value);
            nutrientName.setText(nutrient.getNutrient());
            nutrientValue.setText(nutrient.getValue() + nutrient.getUnit());


            return convertView;
        }

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
