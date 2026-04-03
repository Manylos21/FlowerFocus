package com.example.flowerfocus.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowerfocus.R;
import com.example.flowerfocus.model.SessionWithFlower;
import com.example.flowerfocus.utils.FlowerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FlowerHistoryAdapter extends ListAdapter<SessionWithFlower, FlowerHistoryAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<SessionWithFlower> DIFF =
        new DiffUtil.ItemCallback<SessionWithFlower>() {
            @Override
            public boolean areItemsTheSame(@NonNull SessionWithFlower a, @NonNull SessionWithFlower b) {
                return a.session.id == b.session.id;
            }
            @Override
            public boolean areContentsTheSame(@NonNull SessionWithFlower a, @NonNull SessionWithFlower b) {
                return a.session.status.equals(b.session.status);
            }
        };

    public FlowerHistoryAdapter() { super(DIFF); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flower_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SessionWithFlower item = getItem(position);
        holder.bind(item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final FlowerView flowerView;
        final TextView categoryText;
        final TextView dateText;
        final TextView durationText;
        final TextView statusText;

        ViewHolder(View itemView) {
            super(itemView);
            flowerView   = itemView.findViewById(R.id.itemFlowerView);
            categoryText = itemView.findViewById(R.id.itemCategoryText);
            dateText     = itemView.findViewById(R.id.itemDateText);
            durationText = itemView.findViewById(R.id.itemDurationText);
            statusText   = itemView.findViewById(R.id.itemStatusText);
        }

        void bind(SessionWithFlower item) {
            categoryText.setText(item.session.category);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH);
            dateText.setText(sdf.format(new Date(item.session.date)));

            long minutes = item.session.realDuration / 60;
            durationText.setText(minutes + " min");

            if ("completed".equals(item.session.status)) {
                statusText.setText("✅ Terminée");
            } else {
                statusText.setText("❌ Abandonnée");
            }

            if (item.flower != null) {
                flowerView.setFlowerType(item.flower.type);
                flowerView.setProgress(item.flower.progression);
            }
        }
    }
}
