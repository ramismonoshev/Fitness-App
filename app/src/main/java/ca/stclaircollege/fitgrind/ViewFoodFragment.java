package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import ca.stclaircollege.fitgrind.api.Food;
import ca.stclaircollege.fitgrind.api.FoodAPI;
import ca.stclaircollege.fitgrind.api.Nutrient;
import ca.stclaircollege.fitgrind.database.DatabaseHandler;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class ViewFoodFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String NDBNO_KEY = "ndbno";

    private int currNdbno;
    private Food currFood;

    private FoodAPI foodApi;

    private LinearLayout progressView;
    private ListView mListView;
    private TextView mFoodName, mFoodWeight;
    private Button mAddFoodButton;

    public ViewFoodFragment() {}

    public static ViewFoodFragment newInstance(int ndbno) {
        ViewFoodFragment viewFoodFragment = new ViewFoodFragment();

        Bundle args = new Bundle();
        args.putInt(NDBNO_KEY, ndbno);
        viewFoodFragment.setArguments(args);

        return viewFoodFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currNdbno = getArguments().getInt(NDBNO_KEY);
            foodApi = new FoodAPI(getActivity().getApplicationContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_food, container, false);


        progressView = (LinearLayout) view.findViewById(R.id.progressView);
        mListView = (ListView) view.findViewById(R.id.listview);
        mFoodName = (TextView) view.findViewById(R.id.food_title);
        mFoodWeight = (TextView) view.findViewById(R.id.food_weight);
        mAddFoodButton = (Button) view.findViewById(R.id.addButton);

        if (currNdbno != 0) {
            progressView.setVisibility(View.VISIBLE);
            foodApi.getFood(currNdbno).subscribe(new Observer<Food>() {
                @Override
                public void onSubscribe(Disposable d) { }

                @Override
                public void onNext(Food food) {
                    currFood = food;
                    mFoodName.setText(currFood.getName());
                    mFoodWeight.setText(currFood.getServingSize());
                    mListView.setAdapter(new CustomAdapter(getContext(), currFood.getNutrients()));
                }

                @Override
                public void onError(Throwable e) {
                    progressView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), R.string.invalid_info, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onComplete() { progressView.setVisibility(View.GONE); }
            });
        }

        mAddFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db = new DatabaseHandler(getContext());
                long foodId = db.insertFood(currFood);
                if (foodId != -1 && db.insertFoodLog(foodId) != -1) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction trans = fm.beginTransaction();
                    trans.replace(R.id.content_main, new MainFragment());
                    trans.commit();
                    Toast.makeText(getContext(), R.string.db_insert_success, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), R.string.db_error, Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }



    public class CustomAdapter extends ArrayAdapter<Nutrient> {

        public CustomAdapter(Context context, ArrayList<Nutrient> nutrients) {
            super(context, 0, nutrients);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Nutrient nutrient = getItem(position);
            if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_food_item, parent, false);
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
