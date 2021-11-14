package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.stclaircollege.fitgrind.database.DatabaseHandler;
import ca.stclaircollege.fitgrind.database.Program;


public class  WorkoutProgramFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private static final int LIST_REQUEST = 1;
    ListView list;
    CustomAdapter adapter;
    ArrayList<Program> programsList;


    private OnFragmentInteractionListener mListener;

    public WorkoutProgramFragment() {

    }
    public static WorkoutProgramFragment newInstance(String param1, String param2) {
        WorkoutProgramFragment fragment = new WorkoutProgramFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    FragmentManager fm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_program, container, false);
        fm = getActivity().getSupportFragmentManager();
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabProgram);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Create New Program");
                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_edited_program, null);

                final EditText name = (EditText) dialogView.findViewById(R.id.editNameEditText);
                final EditText desc = (EditText) dialogView.findViewById(R.id.editDescriptionEditText);

                dialog.setView(dialogView);


                dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (name.getText().toString().trim().length() != 0 && desc.getText().toString().trim().length() != 0) {
                            DatabaseHandler db = new DatabaseHandler(getContext());
                            Program program = new Program(name.getText().toString(), desc.getText().toString());
                            long id = db.insertProgram(program);
                            db.close();
                            if (id != -1) {
                                program.setId(id);
                                programsList.add(program);
                                ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                                // create a toast
                                Toast.makeText(getContext(), R.string.db_insert_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), R.string.db_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), R.string.invalid_field, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dialog.show();
            }
        });
        list = (ListView) view.findViewById(R.id.workoutProgramList);
        DatabaseHandler db = new DatabaseHandler(getContext());
        programsList = db.selectAllRoutine();
        db.close();

        adapter = new CustomAdapter(getContext(), programsList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), WorkoutExerciseActivity.class);
                intent.putExtra("id", programsList.get(position).getId());
                startActivity(intent);
            }
        });

        return view;
    }


    public class CustomAdapter extends ArrayAdapter<Program> {
        public CustomAdapter(Context context, ArrayList<Program> items) {
            super(context, 0, items);
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            final Program program = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.program_view, parent, false);
            }


            final TextView name = (TextView) convertView.findViewById(R.id.programName);
            name.setText(program.getName());

            TextView description = (TextView) convertView.findViewById(R.id.programDescription);
            description.setText(program.getDescription());

            final ImageView menuButton = (ImageView) convertView.findViewById(R.id.editProgram);


            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(getContext(), menuButton);
                    menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                    builder.setTitle("Edit " + program.getName());

                                    View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_edited_program, null);

                                    final EditText editText = (EditText) dialogView.findViewById(R.id.editNameEditText);
                                    final EditText editText2 = (EditText) dialogView.findViewById(R.id.editDescriptionEditText);
                                    editText.setText(program.getName());
                                    editText2.setText(program.getDescription());

                                    builder.setView(dialogView);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            program.setName(editText.getText().toString());
                                            program.setDescription(editText2.getText().toString());
                                            DatabaseHandler db = new DatabaseHandler(getContext());
                                            if (db.updateRoutine(program)) {
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
                                    break;

                                case R.id.delete:
                                    DatabaseHandler db = new DatabaseHandler(getContext());
                                    if (db.deleteRoutine(program.getId())) {
                                        programsList.remove(position);
                                        ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                                        Toast.makeText(getContext(), R.string.db_delete_success, Toast.LENGTH_SHORT).show();
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
