package app.seamansbook.tests.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import app.seamansbook.tests.R;
import app.seamansbook.tests.models.QuizResultModel;
import app.seamansbook.tests.viewHolders.StatisticsViewHolder;

public class StatisticsListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<QuizResultModel> quizResultModels;

    public interface OnItemClickListener {
        void onItemClick(String id);
    }

    private final OnItemClickListener listener;
    private final Locale locale;


    public StatisticsListAdapter(Locale locale, List<QuizResultModel> quizResultModels, OnItemClickListener listener) {
        this.locale = locale;
        this.quizResultModels = quizResultModels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_statistics_item, parent, false);
        return new StatisticsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        QuizResultModel quizResultModel = quizResultModels.get(position);
        StatisticsViewHolder statisticsViewHolder = (StatisticsViewHolder) holder;

        statisticsViewHolder.date.setText(quizResultModel.getDate(locale));
        statisticsViewHolder.time.setText(quizResultModel.getTime());
        statisticsViewHolder.score.setText(quizResultModel.getScore() + "%");
        statisticsViewHolder.errorsCount.setText(String.valueOf(quizResultModel.getErrorsCount()));

        statisticsViewHolder.detailedStatisticsButton.setOnClickListener(v -> listener.onItemClick(quizResultModel.getId()));
    }

    @Override
    public int getItemCount() {
        return quizResultModels.size();
    }
}
