package com.example.den.lesson8.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.den.lesson8.Interfaces.PhotoItem;
import com.example.den.lesson8.Interfaces.PhotoItemsPresenterCallbacks;
import com.example.den.lesson8.MainActivity;
import com.example.den.lesson8.R;
import com.example.den.lesson8.ShareActivity;
import com.example.den.lesson8.ShareActivityWithFragments;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolderRecyclerView> {

    PhotoItem[] photoItems;
    PhotoItemsPresenterCallbacks callback;

    public Adapter(PhotoItem[] photoItems, PhotoItemsPresenterCallbacks callback) {
        this.callback = callback;
        this.photoItems = photoItems;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolderRecyclerView onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_item_img, parent, false);
        return new ViewHolderRecyclerView(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderRecyclerView holder, int position) {

        PhotoItem photoItem = this.photoItems[position];

        holder.textViewAuthor.setText(photoItem.getAuthorName());
        Picasso.get().load(photoItem.getImgUrl()).placeholder(R.drawable.placeholder).into(holder.imageViewPhoto);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onItemSelected(photoItem);
            }
        });

        updateFavoriteButton(holder.imageFavorite, photoItem);

        holder.imageFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onItemToggleFavorite(photoItem);
                updateFavoriteButton(holder.imageFavorite,photoItem);
            }
        });
    }



    private void updateFavoriteButton(ImageView imageFavorite, PhotoItem photoItem) {

        boolean isFavorited = photoItem.isSavedToDatabase();
        if(isFavorited) {
            imageFavorite.setImageResource(R.drawable.favorite_on);
        } else {
            imageFavorite.setImageResource(R.drawable.favorite_off);
        }
    }

    @Override
    public int getItemCount() {
        return this.photoItems.length;
    }


    public static class ViewHolderRecyclerView extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView)
        public ImageView imageViewPhoto;
        @BindView(R.id.textViewAuthor)
        public TextView textViewAuthor;
        @BindView(R.id.imageFavorite)
        public ImageView imageFavorite;

        ViewHolderRecyclerView(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
