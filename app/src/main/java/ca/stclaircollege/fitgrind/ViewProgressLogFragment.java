package ca.stclaircollege.fitgrind;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import java.io.File;
import ca.stclaircollege.fitgrind.database.Progress;

public class ViewProgressLogFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Progress mProgress;
    private PhotoView mImageView;

    private OnFragmentInteractionListener mListener;

    public ViewProgressLogFragment() {

    }
    public static ViewProgressLogFragment newInstance(Progress progress) {
        ViewProgressLogFragment fragment = new ViewProgressLogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, progress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProgress = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_progress_log, container, false);

        final ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        mImageView = (PhotoView) view.findViewById(R.id.progress_imageview);


        if (mProgress != null) {
            Picasso.with(getActivity()).load(new File(mProgress.getResource())).into(mImageView);

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bar.isShowing()) {
                        bar.hide();
                    } else {
                        bar.show();
                    }
                }
            });
        }

        return view;
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
