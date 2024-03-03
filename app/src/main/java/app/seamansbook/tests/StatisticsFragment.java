package app.seamansbook.tests;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import app.seamansbook.tests.adapters.StatisticsListAdapter;
import app.seamansbook.tests.data.QuestionScoresDBManager;
import app.seamansbook.tests.models.QuizResultModel;
import app.seamansbook.tests.models.StatisticsMainInfoModel;

public class StatisticsFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        QuestionScoresDBManager scoresDBManager = new QuestionScoresDBManager(requireContext());
        List<QuizResultModel> quizResultModels = scoresDBManager.getQuizResults();

        StatisticsMainInfoModel statisticsMainInfoModel = scoresDBManager.getStatisticsMainInfo();

        TextView successful_questions_count = view.findViewById(R.id.successful_questions_count);
        TextView failed_questions_count = view.findViewById(R.id.failed_questions_count);
        TextView knowledge_level = view.findViewById(R.id.knowledge_level);
        TextView startScore = view.findViewById(R.id.startScore);
        TextView averageScore = view.findViewById(R.id.averageScore);
        TextView bestScore = view.findViewById(R.id.bestScore);

        successful_questions_count.setText(String.valueOf(statisticsMainInfoModel.getSuccessfulTestsCount()));
        failed_questions_count.setText(String.valueOf(statisticsMainInfoModel.getFailedTestsCount()));

        int knowledgeLevel = statisticsMainInfoModel.getAverageScore();
        if(knowledgeLevel < 50) {
            knowledge_level.setText(getResources().getString(R.string.knowledge_level_one));
        } else if(knowledgeLevel < 80) {
            knowledge_level.setText(getResources().getString(R.string.knowledge_level_two));
        } else {
            knowledge_level.setText(getResources().getString(R.string.knowledge_level_three));
        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("seamansbookMain", MODE_PRIVATE);
        int passingPercent = Math.round(sharedPreferences.getFloat("passingPercent", 0));
        int firstScoreValue = statisticsMainInfoModel.getFirstScore();
        int bestScoreValue = statisticsMainInfoModel.getBestScore();

        setTextWithColor(startScore, firstScoreValue, passingPercent);
        setTextWithColor(averageScore, knowledgeLevel, passingPercent);
        setTextWithColor(bestScore, bestScoreValue, passingPercent);



        RecyclerView recyclerView = view.findViewById(R.id.statisticsRecyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        SharedPreferences sharedPreferences2 = requireActivity().getPreferences(MODE_PRIVATE);
        String language = sharedPreferences2.getString("selected_language", "null");
        Locale locale = Locale.getDefault();

        if (!Objects.equals(language, "null")) {
            locale = new Locale(language);
        }

        StatisticsListAdapter statisticsListAdapter = new StatisticsListAdapter(requireContext().getApplicationContext(), locale, quizResultModels, id -> {
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            WrongAnswersFragment detailedStatisticsFragment = new WrongAnswersFragment();
            detailedStatisticsFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, detailedStatisticsFragment).addToBackStack(null).commit();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(statisticsListAdapter);
    }

    private void setTextWithColor(TextView textView, int value, int passingPercent) {
        textView.setText(value > 0 ? value + "%" : "-");
        if(value < passingPercent) {
            textView.setTextColor(Color.parseColor("#D25351"));
        } else {
            textView.setTextColor(Color.parseColor("#00CB47"));
        }
    }
}