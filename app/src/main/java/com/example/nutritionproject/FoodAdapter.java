package com.example.nutritionproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nutritionproject.viewmodel.FoodItem;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    public interface OnAddClickListener {
        void onAddClick(FoodItem item);
    }

    private final List<FoodItem> foodList;
    private final OnAddClickListener listener;

    public FoodAdapter(List<FoodItem> foodList, OnAddClickListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_card, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        FoodItem item = foodList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvCalories.setText(item.getCalories() + " kcal");
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivFood);

//        holder..setOnClickListener(v -> listener.onAddClick(item));
    }

    @Override
    public int getItemCount() { return foodList.size(); }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCalories;
        ImageView ivFood;

        public FoodViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            ivFood = itemView.findViewById(R.id.ivFood);
        }
    }
}
