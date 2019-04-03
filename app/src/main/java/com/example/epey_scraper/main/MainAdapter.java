package com.example.epey_scraper.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.epey_scraper.EpeyUtils.EpeyElement;
import com.example.epey_scraper.R;
import com.example.epey_scraper.info.InfoActivity;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<EpeyElement> epeyElementList;
    private Context mContext;

    MainAdapter(Context mContext, List<EpeyElement> epeyElementList) {
        this.mContext = mContext;
        this.epeyElementList = epeyElementList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cardveiw_element, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int pos) {
        final EpeyElement currentElement = epeyElementList.get(holder.getAdapterPosition());

        holder.elementName.setText(currentElement.getName());

        holder.elementPrice.setText(currentElement.getPrice());

        Glide.with(mContext)
                .load(currentElement.getImage())
                .into(holder.elementImage);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent info = new Intent(mContext, InfoActivity.class);
                info.putExtra("url", epeyElementList.get(holder.getAdapterPosition()).getInfoPageUrl());
                info.putExtra("name", epeyElementList.get(holder.getAdapterPosition()).getName());
                mContext.startActivity(info);
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return epeyElementList.size();
    }

    void add2ElementList(List<EpeyElement> newEpeyElementList) {
        epeyElementList.addAll(newEpeyElementList);
    }

    void clearElementList() {
        epeyElementList.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView elementName = finder(R.id.element_name);
        TextView elementPrice = finder(R.id.element_price);
        ImageView elementImage = finder(R.id.element_img);
        CardView cardView = finder(R.id.item_cardview);


        ViewHolder(@NonNull final View itemView) {
            super(itemView);

        }

        @SuppressWarnings("unchecked")
        private <T> T finder(int id) {
            return (T) itemView.findViewById(id);
        }

    }
}