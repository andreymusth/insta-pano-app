package com.tzkt.andrey.instapano;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tzkt.andrey.instapano.limiter.LimiterView;
import com.tzkt.andrey.instapano.utils.BitmapUtils;
import com.tzkt.andrey.instapano.limiter.Colors;

import java.io.IOException;

/**
 * Created by andrey on 03/11/2017.
 */

public class EditorFragment extends Fragment implements View.OnClickListener, LimiterView.OnDoubleTapListener{

    private static final String IMAGE_PATH = "com.tzkt.andrey.instapano.IMAGE_PATH";

    private static final int MIN_PARTS_QUANTITY = 2;
    private static final int MAX_PARTS_QUANTITY = 10;

    private LimiterView mLimiterView;
    private Callbacks mCallbacks;

    private Bitmap mBitmap;

    private Uri mImagePath;
    private FrameLayout mContainer;
    private FloatingActionButton mAddBtn, mRemoveBtn;
    private TextView mCurrentParts;

    private int mPartsQuantity = 2;

    public static EditorFragment newInstance(String imagePath) {

        Bundle args = new Bundle();
        args.putSerializable(IMAGE_PATH, imagePath);
        EditorFragment fragment = new EditorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mImagePath = Uri.parse(bundle.getString(IMAGE_PATH));
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        BitmapUtils.imgs = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (android.R.id.home == itemId) {
            getActivity().onBackPressed();
        }

        if (R.id.action_preview == itemId) {
            onDoubleTap();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.editor, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_editor, container, false);
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver() , mImagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCurrentParts = v.findViewById(R.id.tv_current_parts);
        mCurrentParts.setText(String.valueOf(mPartsQuantity));

        mAddBtn = v.findViewById(R.id.btn_plus);
        mAddBtn.setOnClickListener(this);

        mRemoveBtn = v.findViewById(R.id.btn_minus);
        mRemoveBtn.setOnClickListener(this);

        if (mBitmap != null) {
            mLimiterView = new LimiterView(getActivity());
            mLimiterView.setOnDoubleTapListener(this);
            mContainer = v.findViewById(R.id.fl_limiter_container);
            //mPicture.setImageBitmap(bitmap);
            mContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mLimiterView.init(mBitmap, mPartsQuantity, mContainer.getTop(), mContainer.getBottom(), mContainer.getLeft(), mContainer.getRight());
                }
            });

        }
        mLimiterView.setZ(10f);
        mContainer.addView(mLimiterView, 0);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_plus:
                mPartsQuantity += 1;
                mPartsQuantity = mPartsQuantity > MAX_PARTS_QUANTITY ? MAX_PARTS_QUANTITY : mPartsQuantity;
                break;
            case R.id.btn_minus:
                mPartsQuantity -= 1;
                mPartsQuantity = mPartsQuantity < MIN_PARTS_QUANTITY ? MIN_PARTS_QUANTITY : mPartsQuantity;
                break;
        }
        mLimiterView.setPartsQuantity(mPartsQuantity);
        mCurrentParts.setText(String.valueOf(mPartsQuantity));
        startAnimation();
    }

    private void startAnimation()
    {
        Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.text_popup);
        a.reset();
        mCurrentParts.clearAnimation();
        mCurrentParts.startAnimation(a);
    }

    @Override
    public void onDoubleTap() {

        if (mLimiterView.getPaintColor() == Colors.GREEN) {

            BitmapUtils.splitBitmap(
                    mBitmap,
                    mLimiterView.getScaledImage(),
                    mLimiterView.getLeftTop(),
                    mLimiterView.getLeftBottom(),
                    mLimiterView.getRightTop(),
                    mLimiterView.getLeftEdgeOfImage(),
                    mLimiterView.getTopEdgeOfImage(),
                    mPartsQuantity);
            mCallbacks.onButtonClicked(R.id.action_preview);
        } else {
            Toast.makeText(getActivity(), getString(R.string.cut_error), Toast.LENGTH_LONG).show();
        }
    }

    public interface Callbacks {
        public void onButtonClicked(int viewId);
    }
}
