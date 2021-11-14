package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import ca.stclaircollege.fitgrind.api.Item;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;


public class AddFoodFragment extends Fragment {

    private OnFragmentInteractionListener mListener;


    private FloatingActionButton searchButton;
    private EditText searchField;
    private LinearLayout progressBar;


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;


    private FoodAPI foodApi;


    private int start, end, total;

    private ArrayList<Item> itemList = new ArrayList<Item>();

    public AddFoodFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        foodApi = new FoodAPI(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_food, container, false);


        searchButton = (FloatingActionButton) view.findViewById(R.id.searchButton);
        searchField = (EditText) view.findViewById(R.id.searchField);
        progressBar = (LinearLayout) view.findViewById(R.id.progressBar);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);


        mRecyclerView.setHasFixedSize(true);


        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);


        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    dismissKeyboard();
                    if (searchField.getText().length() != 0) searchFood();
                    return true;
                }
                return false;
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissKeyboard();

                if (searchField.getText().length() != 0) searchFood();

            }
        });



        return view;
    }


    private void dismissKeyboard() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void searchFood(){

        progressBar.setVisibility(View.VISIBLE);
        Observable<ArrayList<Item>> itemObservable = foodApi.searchFood(searchField.getText().toString());
        itemObservable.subscribe(new Observer<ArrayList<Item>>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(ArrayList<Item> items) {
                mRecyclerView.setAdapter(new MyAdapter(items));
            }

            @Override
            public void onError(Throwable e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() { progressBar.setVisibility(View.GONE); }
        });
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<Item> mDataset;


        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView name, group;

            public ViewHolder(View view) {
                super(view);

                this.name = (TextView) view.findViewById(R.id.name);
                this.group = (TextView) view.findViewById(R.id.group);
            }

            public TextView getNameTextView() { return this.name; }
            public TextView getGroupTextView() { return this.group; }

        }


        public MyAdapter(ArrayList<Item> myDataset) {
            mDataset = myDataset;
        }


        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_layout, parent, false);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    searchField.setText("");

                    int position = mRecyclerView.getChildLayoutPosition(v);

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction trans = fm.beginTransaction();

                    trans.replace(R.id.content_main, ViewFoodFragment.newInstance(mDataset.get(position).getNdbno()));
                    trans.addToBackStack(null);
                    trans.commit();
                }
            });


            return new ViewHolder(view);

        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.getNameTextView().setText(mDataset.get(position).getName());
            holder.getGroupTextView().setText(mDataset.get(position).getGroup());

        }


        @Override
        public int getItemCount() {
            return mDataset.size();
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
