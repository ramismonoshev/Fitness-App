package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import ca.stclaircollege.fitgrind.database.Cardio;
import ca.stclaircollege.fitgrind.database.DatabaseHandler;
import ca.stclaircollege.fitgrind.database.Strength;
import ca.stclaircollege.fitgrind.database.WorkoutType;


public class ExerciseFragment extends Fragment {

    private static final String ARG_PARAM = "param";
    private static final String ARG_PARAM2 = "param2";
    private static final int ADD_EXERCISE_REQUEST = 1;


    private int mParam2;
    private long mParam;

    ListView list;
    CustomAdapter customAdapter;
    ArrayList<WorkoutType> exercisesList;

    private OnFragmentInteractionListener mListener;

    public ExerciseFragment() {

    }



    public static ExerciseFragment newInstance(int param2, long param) {
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM2, param2);
        args.putLong(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam2 = getArguments().getInt(ARG_PARAM2);
            mParam = getArguments().getLong(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        list = (ListView) view.findViewById(R.id.exerciselist);
        DatabaseHandler db = new DatabaseHandler(getContext());

        exercisesList = db.selectAllWorkoutAt(mParam2, mParam);
        db.close();

        customAdapter = new CustomAdapter(getContext(), exercisesList);
        list.setAdapter(customAdapter);

        return view;
    }

    public class CustomAdapter extends ArrayAdapter<WorkoutType> {
        public CustomAdapter(Context context, ArrayList<WorkoutType> items) {
            super(context, 0, items);
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            final WorkoutType item = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.exercise_view, parent, false);
            }
            LinearLayout strengthLayout = (LinearLayout) convertView.findViewById(R.id.strengthLayout);
            LinearLayout cardioLayout = (LinearLayout) convertView.findViewById(R.id.cardioLayout);


            final TextView exerciseName = (TextView) convertView.findViewById(R.id.exerciseName);
            exerciseName.setText(item.getName());

            if(item instanceof Strength) {
                Strength mItem = (Strength) item;
                TextView set = (TextView) convertView.findViewById(R.id.exerciseSet);
                set.setText("" + mItem.getSet());

                TextView rep = (TextView) convertView.findViewById(R.id.exerciseRep);
                rep.setText("" + mItem.getReptitions());

                TextView weight = (TextView) convertView.findViewById(R.id.exerciseWeight);
                weight.setText("" + mItem.getWeight() + " lbs");


                cardioLayout.setVisibility(View.GONE);
                strengthLayout.setVisibility(View.VISIBLE);

            } else if (item instanceof Cardio){
                Cardio mItem = (Cardio) item;
                TextView time = (TextView) convertView.findViewById(R.id.exerciseTime);
                time.setText("" + mItem.getTime() + " minutes");


                strengthLayout.setVisibility(View.GONE);
                cardioLayout.setVisibility(View.VISIBLE);
            }


            final ImageView menuButton = (ImageView) convertView.findViewById(R.id.editExercise);

            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu menu = new PopupMenu(getContext(), menuButton);
                    menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem mItem) {
                            switch (mItem.getItemId()) {
                                case R.id.edit:
                                    if (item instanceof Strength) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                        builder.setTitle("Edit " + ((Strength) item).getName());
                                        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_edited_exercise, null);
                                        final EditText editText = (EditText) dialogView.findViewById(R.id.editNameEditText);
                                        final EditText editText2 = (EditText) dialogView.findViewById(R.id.editSetEditText);
                                        final EditText editText3 = (EditText) dialogView.findViewById(R.id.editRepEditText);
                                        final EditText editText4 = (EditText) dialogView.findViewById(R.id.editWeightEditText);

                                        editText.setText(((Strength) item).getName());
                                        editText2.setText(""+((Strength) item).getSet());
                                        editText3.setText(""+((Strength) item).getReptitions());
                                        editText4.setText(""+(((Strength) item).getWeight()));

                                        builder.setView(dialogView);

                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ((Strength) item).setName(editText.getText().toString());
                                                ((Strength) item).setSet(Integer.parseInt(editText2.getText().toString()));
                                                ((Strength) item).setReptitions(Integer.parseInt(editText3.getText().toString()));
                                                ((Strength) item).setWeight(Double.parseDouble(editText4.getText().toString()));
                                                DatabaseHandler db = new DatabaseHandler(getContext());

                                                if (db.updateWorkout((Strength) item)) {
                                                    ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                                                    Toast.makeText(getContext(), R.string.db_update_success, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.show();

                                    } else if (item instanceof Cardio){

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                        builder.setTitle("Edit " + ((Cardio) item).getName());

                                        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_edited_cardio, null);
                                        final EditText editText = (EditText) dialogView.findViewById(R.id.editNameEditText);
                                        final EditText editText2 = (EditText) dialogView.findViewById(R.id.editTimeEditText);


                                        editText.setText(item.getName());
                                        editText2.setText(""+(((Cardio) item).getTime()));

                                        builder.setView(dialogView);

                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ((Cardio) item).setName(editText.getText().toString());
                                                ((Cardio) item).setTime(Double.parseDouble(editText2.getText().toString()));

                                                DatabaseHandler db = new DatabaseHandler(getContext());

                                                if (db.updateWorkout((Cardio) item)) {
                                                    ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                                                    Toast.makeText(getContext(), R.string.db_update_success, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.show();
                                    }
                                    break;
                                case R.id.delete:
                                    DatabaseHandler db = new DatabaseHandler(getContext());
                                    if(item instanceof Strength) {
                                        if (db.deleteStrengthWorkout(((Strength) item).getStrengthId())) {
                                            exercisesList.remove(position);

                                            ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                                            Toast.makeText(getContext(), R.string.db_delete_success, Toast.LENGTH_SHORT).show();
                                    }
                                    } else if (item instanceof Cardio) {
                                        if (db.deleteCardioWorkout(((Cardio) item).getCardioId())) {
                                            exercisesList.remove(position);
                                            ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                                            Toast.makeText(getContext(), R.string.db_delete_success, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    db.close();
                                    break;
                            }

                            return true;
                        }
                    });

                    menu.show();
                }

            });

            return  convertView;
        }
    }

    public void addItem(WorkoutType item) {
        exercisesList.add(item);
        ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
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
