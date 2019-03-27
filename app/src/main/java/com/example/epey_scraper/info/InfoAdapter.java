package com.example.epey_scraper.info;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.epey_scraper.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {
    private static final String TAG = "InfoAdapter";
    private static final int UNSELECTED = -1;

    private Context mContext;
    private RecyclerView recyclerView;
    private List<String> catagoryImageUrl;
    private List<String> catagoryTitles;
    private List<String> catagoryDetails;
    private int selectedItem = UNSELECTED;

    InfoAdapter(Context mContext, RecyclerView recyclerView, List<String> catagoryImageUrl, List<String> catagoryTitles, List<String> catagoryDetails) {
        this.mContext = mContext;
        this.recyclerView = recyclerView;
        this.catagoryImageUrl = catagoryImageUrl;
        this.catagoryTitles = catagoryTitles;
        this.catagoryDetails = catagoryDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_info_element, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return catagoryTitles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
        private ImageView imageView;
        private ExpandableLayout expandableLayout;
        private TextView expandButton, info_txt;

        ViewHolder(View itemView) {
            super(itemView);

            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            expandableLayout.setInterpolator(new LinearInterpolator());
            expandableLayout.setOnExpansionUpdateListener(this);

            imageView = itemView.findViewById(R.id.info_icon);
            info_txt = itemView.findViewById(R.id.info_txt);
            expandButton = itemView.findViewById(R.id.expand_button);
            expandButton.setOnClickListener(this);
        }

        void bind() {
            int position = getAdapterPosition();
            boolean isSelected = position == selectedItem;

            Spanned spanned = Html.fromHtml(catagoryDetails.get(position));
            info_txt.setText(spanned);

            Glide.with(mContext)
                    .load(catagoryImageUrl.get(position).replace("https://", "http://"))
                    .into(imageView);

            expandButton.setText(catagoryTitles.get(position));
            expandButton.setSelected(isSelected);
            expandableLayout.setExpanded(isSelected, false);
        }

        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
            if (holder != null) {
                holder.expandButton.setSelected(false);
                holder.expandableLayout.collapse();
            }

            int position = getAdapterPosition();
            if (position == selectedItem) {
                selectedItem = UNSELECTED;
            } else {
                expandButton.setSelected(true);
                expandableLayout.expand();
                selectedItem = position;
            }
        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            Log.d("ExpandableLayout", "State: " + state);
//            if (state == ExpandableLayout.State.EXPANDING) {
//                try {
//                    recyclerView.smoothScrollToPosition(getAdapterPosition());
//                } catch (IllegalArgumentException e) {
//                    Log.d(TAG, "onExpansionUpdate: " + e);
//                }
//            }
        }
    }
}