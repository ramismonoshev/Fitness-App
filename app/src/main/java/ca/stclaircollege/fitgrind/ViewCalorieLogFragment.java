package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import ca.stclaircollege.fitgrind.database.DatabaseHandler;
import ca.stclaircollege.fitgrind.database.FoodLog;

public class ViewCalorieLogFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private SectionPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private TextView currentDate;
    private ImageButton backButton, forwardButton;

    public ViewCalorieLogFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_calorie_log, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.calorie_viewpager);
        currentDate = (TextView) view.findViewById(R.id.calorielog_date);
        backButton = (ImageButton) view.findViewById(R.id.calorie_back_button);
        forwardButton = (ImageButton) view.findViewById(R.id.calorie_forward_button);

        mPagerAdapter = new SectionPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setCurrentItem(2);
        currentDate.setText("Today");

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: currentDate.setText("2 Days Ago"); break;
                    case 1: currentDate.setText("Yesterday"); break;
                    default: currentDate.setText("Today"); break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1, true);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1, true);
            }
        });

        return view;
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<FoodLog> foodLog = new ArrayList<FoodLog>();
        private double[][] nutrients;

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);

            DatabaseHandler db = new DatabaseHandler(getContext());
            foodLog.add(db.selectCalorieLogAt(2));
            foodLog.add(db.selectCalorieLogAt(1));
            foodLog.add(db.selectCalorieLogAt(0));
            db.close();
        }
        public Fragment getItem(int position) {
            switch(position) {
                case 0: return ViewCalorieDayLogFragment.newInstance(foodLog.get(0), 2);
                case 1: return ViewCalorieDayLogFragment.newInstance(foodLog.get(1), 1);
                default: return ViewCalorieDayLogFragment.newInstance(foodLog.get(2), 0);
            }
        }
        public int getCount(){
            return foodLog.size();
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
