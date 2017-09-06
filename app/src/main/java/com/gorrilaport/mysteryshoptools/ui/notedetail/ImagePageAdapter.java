package com.gorrilaport.mysteryshoptools.ui.notedetail;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gorrilaport.mysteryshoptools.R;

import java.util.ArrayList;

public class ImagePageAdapter extends PagerAdapter{
    Context mContext;
    ArrayList<String> mImages;
    LayoutInflater mLayoutInflater;
    private final NoteDetailContract.View mView;
    private boolean editMode = false;

    public ImagePageAdapter(Context context, ArrayList<String> images, NoteDetailContract.View mView){
        this.mContext = context;
        this.mImages = images;
        this.mView = mView;
        mLayoutInflater = LayoutInflater.from(context);
    }
    
    public ImagePageAdapter(Context context, ArrayList<String> images){
        this.mContext = context;
        this.mImages = images;
        mLayoutInflater = LayoutInflater.from(context);
        this.editMode = true;
        mView = null;
    }
    
    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        Glide.with(mContext.getApplicationContext())
                .load(mImages.get(position))
                .into(imageView);

        container.addView(itemView);

        //listening to image click.
       if (editMode == false) {
           imageView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   mView.displayFullImage(mImages.get(position));
               }
           });
       }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
