package com.tzkt.andrey.instapano;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by andrey on 03/11/2017.
 */

public class EnterPageFragment extends Fragment implements View.OnClickListener{

    private int NUMBER_OF_SLICES = 2;
    private Callbacks mCallbacks;

    @Override
    public void onStart() {
        super.onStart();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static EnterPageFragment newInstance() {

        return new EnterPageFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_enter_page, container, false);

        Button choosePhotoButton = v.findViewById(R.id.btn_choose_photo);
        choosePhotoButton.setOnClickListener(this);

        Button takePictureButton = v.findViewById(R.id.btn_take_photo);
        takePictureButton.setOnClickListener(this);
        
        return v;
    }

    @Override
    public void onClick(View view) {
        mCallbacks.onButtonClicked(view.getId());
    }

    public interface Callbacks {
        void onButtonClicked(int viewId);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//
//        inflater.inflate(R.menu.main, menu);
//    }
}
