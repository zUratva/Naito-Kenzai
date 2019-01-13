package com.aizoban.naitokenzai.utils;

import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;
import com.aizoban.naitokenzai.utils.wrappers.PaletteBitmapWrapper;

public class PaletteBitmapTarget extends ImageViewTarget<PaletteBitmapWrapper> {
    public PaletteBitmapTarget(ImageView view) {
        super(view);
    }

    @Override
    protected void setResource(PaletteBitmapWrapper paletteBitmapWrapper) {
        view.setImageBitmap(paletteBitmapWrapper.getBitmap());
    }
}
