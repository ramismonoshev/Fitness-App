package ca.stclaircollege.fitgrind;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import com.github.clans.fab.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import ca.stclaircollege.fitgrind.database.DatabaseHandler;
import ca.stclaircollege.fitgrind.database.Progress;
import ca.stclaircollege.fitgrind.database.Weight;


public class WeightLogFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int GALLERY_INTENT = 1;
    private static final int CAMERA_INTENT = 2;

    private static final int STORAGE_REQUEST = 1;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView mCurrentWeight, mWeightGoal;
    private Button mViewProgressButton;
    private ArrayList<Weight> weightList;
    private WeightCalculator weightCalculator;
    private ListView mListView;
    private FloatingActionButton fab;
    private String mCurrentPhotoPath;
    private long weightId = -1;

    public WeightLogFragment() {}

    public static WeightLogFragment newInstance(String param1, String param2) {
        WeightLogFragment fragment = new WeightLogFragment();
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

        weightCalculator = new WeightCalculator(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight_log, container, false);

        mCurrentWeight = (TextView) view.findViewById(R.id.current_weight_label);
        mWeightGoal = (TextView) view.findViewById(R.id.weight_goal_label);
        mViewProgressButton = (Button) view.findViewById(R.id.viewProgressButton);
        mListView = (ListView) view.findViewById(R.id.listview_weight);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        DatabaseHandler db = new DatabaseHandler(getContext());
        weightList = db.selectAllWeightLog();
        db.close();

        CustomAdapter adapter = new CustomAdapter(getContext(), weightList);
        mListView.setAdapter(adapter);

        mCurrentWeight.setText("Current Weight: " + weightCalculator.getCurrentWeight());

        mWeightGoal.setText("Weight Goal: " + weightCalculator.getWeightGoal());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseHandler db = new DatabaseHandler(getContext());
                final String lastDate = db.lastRecordedWeightLog();
                db.close();
                long diff = (lastDate != null) ? 0 : 7;
                if (lastDate != null) {
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastDate);
                        Date now = Calendar.getInstance(Locale.getDefault()).getTime();
                        diff = now.getTime() - date.getTime();
                        diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (diff >= 7) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("Add Weight Log");
                    dialog.setMessage("Enter in your current weight:");

                    final EditText input = new EditText(getContext());

                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    dialog.setView(input);


                    dialog.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Double weight = Double.parseDouble(input.getText().toString());
                            Calendar cal = Calendar.getInstance(Locale.getDefault());
                            String currDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
                            Weight weightLog = new Weight(weight, currDate);
                            DatabaseHandler db = new DatabaseHandler(getContext());
                            long id = db.insertWeight(weightLog);
                            db.close();
                            if (id != -1) {
                                weightLog.setId(id);
                                weightList.add(weightLog);
                                ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                SharedPreferences.Editor edit = SP.edit();
                                edit.putString("weight", ""+weight);
                                edit.commit();
                                mCurrentWeight.setText(String.format("Current Weight: %.1f lbs", weight));
                                Toast.makeText(getActivity(), R.string.db_insert_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.db_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { dialogInterface.dismiss(); }
                    });
                    dialog.show();

                } else {
                    Toast.makeText(getActivity(), String.format(getString(R.string.invalid_days), 7 - diff), Toast.LENGTH_LONG).show();
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Weight weight = (Weight) mListView.getItemAtPosition(i);
                DatabaseHandler db = new DatabaseHandler(getContext());
                Progress progress = db.selectProgress(weight.getId());
                db.close();
                if (progress == null) {
                    weightId = weight.getId();
                    requestStoragePermissions();
                } else {
                    Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);
                    intent.putExtra("progress", progress);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });


        mViewProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db = new DatabaseHandler(getContext());
                if (db.isProgressEmpty()) {
                    Toast.makeText(getActivity(), R.string.db_empty, Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getActivity(), ViewProgressActivity.class);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
                db.close();
            }
        });

        return view;
    }

    public class CustomAdapter extends ArrayAdapter<Weight> {

        public CustomAdapter(Context context, ArrayList<Weight> weightList) {
            super(context, 0, weightList);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Weight weightItem = getItem(position);

            if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_weight_log, parent, false);
            TextView date = (TextView) convertView.findViewById(R.id.recorded_date);
            final TextView weight = (TextView) convertView.findViewById(R.id.recorded_weight);
            date.setText(weightItem.getFormattedDate());
            weight.setText(weightItem.getWeight() + "lbs");

            final ImageView menuButton = (ImageView) convertView.findViewById(R.id.menuButton);

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
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                                    dialog.setTitle("Edit Weight Log");
                                    dialog.setMessage("Edit weight log\'s weight.");
                                    final EditText input = new EditText(getContext());
                                    input.setText("" + weightItem.getWeight());
                                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                    dialog.setView(input);

                                    dialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            DatabaseHandler db = new DatabaseHandler(getContext());
                                            Double parsedWeight = Double.parseDouble(input.getText().toString());
                                            weightItem.setWeight(parsedWeight);
                                            if (db.updateWeight(weightItem)) {
                                                ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                                Toast.makeText(getActivity(), R.string.db_update_success, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) { dialogInterface.dismiss(); }
                                    });

                                    dialog.show();

                                    break;
                                case R.id.delete:
                                    DatabaseHandler db = new DatabaseHandler(getContext());
                                    if (db.deleteWeight(weightItem.getId())) {
                                        weightList.remove(position);
                                        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                                        db.deleteProgressByWeight(weightItem.getId());
                                        Toast.makeText(getActivity(), R.string.db_delete_success, Toast.LENGTH_SHORT).show();
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
            return convertView;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_INTENT && resultCode == getActivity().RESULT_OK) {
            String path = getPath(getContext(), data.getData());
            if (path != null) {
                DatabaseHandler db = new DatabaseHandler(getContext());
                boolean result = db.insertProgress(new Progress(path),weightId);
                if (result) {
                    Toast.makeText(getActivity(), R.string.db_insert_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.db_error, Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA_INTENT && resultCode == getActivity().RESULT_OK) {
            DatabaseHandler db = new DatabaseHandler(getContext());
            boolean result = db.insertProgress(new Progress(mCurrentPhotoPath), weightId);
            if (result) {
                Toast.makeText(getActivity(), R.string.db_insert_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.db_error, Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            openPhotoDialog();
        }
    }

    private String getPath( Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        return result;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void openPhotoDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };
        dialog.setTitle("Select Option");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch(IOException e) { e.printStackTrace(); }
                                if (photoFile != null) {
                                    Uri photoURI = FileProvider.getUriForFile(getContext(), "ca.stclaircollege.fileprovider", photoFile);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(intent, CAMERA_INTENT);
                                }
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivityForResult(intent, GALLERY_INTENT);
                            }
                        }
                        break;
                    case 1:
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(intent, GALLERY_INTENT);
                        }
                        break;
                    default:
                        dialogInterface.dismiss();
                        break;
                }
            }
        });
        dialog.show();
    }

    public void requestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openPhotoDialog();
            } else {
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST);
            }
        } else {
            openPhotoDialog();
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
