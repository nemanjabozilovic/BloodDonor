package com.example.blooddonor.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.blooddonor.R;
import com.example.blooddonor.domain.models.FaqDTO;
import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.ViewHolder> {

    private final List<FaqDTO> faqList;
    private int expandedPosition = -1;

    public FaqAdapter(List<FaqDTO> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FaqDTO faq = faqList.get(position);

        holder.faqQuestion.setText(faq.getQuestion());
        holder.faqAnswer.setText(faq.getAnswer());

        final boolean isExpanded = position == expandedPosition;
        holder.faqAnswer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.expandIcon.setImageResource(isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);

        holder.itemView.setOnClickListener(v -> {
            expandedPosition = isExpanded ? -1 : position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView faqQuestion, faqAnswer;
        private final ImageView expandIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            faqQuestion = itemView.findViewById(R.id.faq_question);
            faqAnswer = itemView.findViewById(R.id.faq_answer);
            expandIcon = itemView.findViewById(R.id.expand_icon);
        }
    }
}