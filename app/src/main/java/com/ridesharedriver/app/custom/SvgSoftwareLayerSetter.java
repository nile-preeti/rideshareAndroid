package com.ridesharedriver.app.custom;

import android.graphics.drawable.PictureDrawable;
import android.view.View;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class SvgSoftwareLayerSetter implements RequestListener<PictureDrawable> {

    private ImageView imageView; // Add this line

    public SvgSoftwareLayerSetter(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public boolean onLoadFailed(
            @Nullable GlideException e,
            Object model,
            Target<PictureDrawable> target,
            boolean isFirstResource
    ) {
        return false;
    }

    @Override
    public boolean onResourceReady(
            PictureDrawable resource,
            Object model,
            Target<PictureDrawable> target,
            DataSource dataSource,
            boolean isFirstResource
    ) {
        if (resource != null) {
            // Set the PictureDrawable to the ImageView
            imageView.setImageDrawable(resource);
            imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        return false;
    }
}




