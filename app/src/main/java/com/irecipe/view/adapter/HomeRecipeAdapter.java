package com.irecipe.view.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.irecipe.R;
import com.irecipe.models.RecipeModel;
import com.irecipe.utils.Constants;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import io.paperdb.Paper;

public class HomeRecipeAdapter extends RecyclerView.Adapter<HomeRecipeAdapter.ViewHolder> {

    public ArrayList<RecipeModel> recipeList;
    private LayoutInflater mInflater;
    private final ItemClickListener itemClickListener;

    private Boolean isFavourite;

    public HomeRecipeAdapter(ArrayList<RecipeModel> recipeList, ItemClickListener itemClickListener, Boolean isFavourite) {
        this.recipeList = recipeList;
        this.itemClickListener = itemClickListener;
        this.isFavourite = isFavourite;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recipe_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecipeModel recipeModel = recipeList.get(position);
        holder.dishNameText.setText(recipeModel.getDishName());
        holder.ingredientText.setText(recipeModel.getDishIngredient());
        holder.recipeUserName.setText(recipeModel.getUploadedUserName());

        if (recipeModel.getDishUploadedImage().isEmpty())
            holder.recipeImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.background_image));
        else
            Glide.with(holder.itemView.getContext()).load(recipeModel.getDishUploadedImage()).into(holder.recipeImage);

        holder.likeButton.setLiked(isFavourite);

        if (!isFavourite && recipeList.get(position).getFavouriteRecipe())
            holder.likeButton.setLiked(true);

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                itemClickListener.likedClicked(recipeModel, true);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                itemClickListener.likedClicked(recipeModel, false);

            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView recipeImage;
        TextView dishNameText;
        TextView ingredientText;
        LikeButton likeButton;
        TextView recipeUserName;

        ViewHolder(View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.iv_recipe_img);
            dishNameText = itemView.findViewById(R.id.dish_name_text);
            ingredientText = itemView.findViewById(R.id.ingredient_text);
            likeButton = itemView.findViewById(R.id.like_hearted_button);
            recipeUserName = itemView.findViewById(R.id.recipe_upload_user_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }


    public interface ItemClickListener {
        void onItemClick(int position);

        void likedClicked(RecipeModel recipeModel, Boolean isFlag);
    }
}
