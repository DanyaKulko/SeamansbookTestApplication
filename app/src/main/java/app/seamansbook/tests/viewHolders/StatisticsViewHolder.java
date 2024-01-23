package app.seamansbook.tests.viewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.seamansbook.tests.R;

public class StatisticsViewHolder extends RecyclerView.ViewHolder {
    public TextView date;
    public TextView time;
    public TextView score;
    public TextView errorsCount;
    public ImageView detailedStatisticsButton;

    public StatisticsViewHolder(@NonNull View itemView) {
        super(itemView);
        this.date = itemView.findViewById(R.id.dateTextView);
        this.time = itemView.findViewById(R.id.timeTextView);
        this.score = itemView.findViewById(R.id.scoreTextView);
        this.errorsCount = itemView.findViewById(R.id.errorsCountTextView);
        this.detailedStatisticsButton = itemView.findViewById(R.id.detailedStatisticsButton);
    }
}
