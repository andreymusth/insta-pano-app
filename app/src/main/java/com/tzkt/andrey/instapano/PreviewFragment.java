package com.tzkt.andrey.instapano;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by andrey on 28/01/2018.
 */

public class PreviewFragment extends Fragment {

    Bitmap mImagePiece;

    public static PreviewFragment newInstance(Bitmap bitmap) {

        PreviewFragment fragment = new PreviewFragment();
        fragment.setImagePiece(bitmap);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_preview, container, false);

        ImageView instagramPiece = v.findViewById(R.id.iv_piece);
        instagramPiece.setImageBitmap(mImagePiece);

        return v;

    }

    public void setImagePiece(Bitmap imagePiece) {
        mImagePiece = imagePiece;
    }
}
