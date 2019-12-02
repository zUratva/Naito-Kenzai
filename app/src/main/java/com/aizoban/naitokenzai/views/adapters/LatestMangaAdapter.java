package com.aizoban.naitokenzai.views.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.aizoban.naitokenzai.R;
import com.aizoban.naitokenzai.models.Manga;
import com.aizoban.naitokenzai.utils.PaletteBitmapTarget;
import com.aizoban.naitokenzai.utils.PaletteBitmapTranscoder;
import com.aizoban.naitokenzai.utils.PaletteUtils;
import com.aizoban.naitokenzai.utils.wrappers.PaletteBitmapWrapper;

public class LatestMangaAdapter extends BaseCursorAdapter {
    private OnLatestPositionListener mOnLatestPositionListener;

    public LatestMangaAdapter(Context context) {
        super(context, Manga.class);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mCursor == null) {
            throw new IllegalStateException("Null Cursor");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Can't Move Cursor to Position " + position);
        }

        if (mOnLatestPositionListener != null) {
            mOnLatestPositionListener.onLatestPosition(position);
        }

        ViewHolder viewHolder;
        View currentView = convertView;

        if (currentView == null) {
            currentView = LayoutInflater.from(mContext).inflate(R.layout.item_latest_manga, parent, false);
            viewHolder = new ViewHolder(currentView);
            currentView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) currentView.getTag();
        }

        Manga currentManga = (Manga) getItem(position);
        viewHolder.renderView(mContext, currentManga);

        return currentView;
    }

    public void setOnLatestPositionListener(OnLatestPositionListener onLatestPositionListener) {
        mOnLatestPositionListener = onLatestPositionListener;
    }

    public interface OnLatestPositionListener {
        public void onLatestPosition(int position);
    }

    private static class ViewHolder {
        private ImageView mThumbnailImageView;
        private View mMaskView;
        private TextView mNumberTextView;
        private TextView mNameTextView;
        private LinearLayout mFooterView;

        public ViewHolder(View itemView) {
            mThumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnailImageView);
            mMaskView = itemView.findViewById(R.id.maskImageView);
            mNumberTextView = (TextView) itemView.findViewById(R.id.numberTextView);
            mNameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            mFooterView = (LinearLayout) itemView.findViewById(R.id.footerLinearLayout);
        }

        public void renderView(Context context, Manga manga) {
            setName(manga.getName());
            setNumber(manga.getUpdateCount());
            setMask(context.getResources().getColor(R.color.Black500));
            setFooter(context.getResources().getColor(R.color.Black500));
            setThumbnail(context, manga.getThumbnailUrl(), context.getResources().getColor(R.color.accentPinkA200));
        }

        private void setThumbnail(Context context, String thumbnailUrl, final int defaultColor) {
            mThumbnailImageView.setScaleType(ImageView.ScaleType.CENTER);

            Drawable placeHolderDrawable = context.getResources().getDrawable(R.drawable.ic_image_white_48dp);
            placeHolderDrawable.setColorFilter(defaultColor, PorterDuff.Mode.MULTIPLY);
            Drawable errorHolderDrawable = context.getResources().getDrawable(R.drawable.ic_error_white_48dp);
            errorHolderDrawable.setColorFilter(defaultColor, PorterDuff.Mode.MULTIPLY);

            Glide.with(context)
                    .load(thumbnailUrl)
                    .asBitmap()
                    .transcode(new PaletteBitmapTranscoder(), PaletteBitmapWrapper.class)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .animate(android.R.anim.fade_in)
                    .placeholder(placeHolderDrawable)
                    .error(errorHolderDrawable)
                    .into(new PaletteBitmapTarget(mThumbnailImageView) {
                        @Override
                        public void onResourceReady(PaletteBitmapWrapper resource, GlideAnimation<? super PaletteBitmapWrapper> glideAnimation) {
                            mThumbnailImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            super.onResourceReady(resource, glideAnimation);

                            int color = PaletteUtils.getColorWithDefault(resource.getPalette(), defaultColor);
                            setMask(color);
                            setFooter(color);
                        }
                    });
        }

        private void setMask(int color) {
            GradientDrawable maskDrawable = new GradientDrawable();
            maskDrawable.setColor(color);
            mMaskView.setBackgroundDrawable(maskDrawable);
        }

        private void setNumber(int number) {
            mNumberTextView.setText(Integer.toString(number));
        }

        private void setName(String name) {
            mNameTextView.setText(name);
        }

        private void setFooter(int color) {
            GradientDrawable footerDrawable = new GradientDrawable();
            footerDrawable.setCornerRadii(new float[]{0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 4.0f, 4.0f, 4.0f});
            footerDrawable.setColor(color);
            mFooterView.setBackgroundDrawable(footerDrawable);
        }
    }
}
