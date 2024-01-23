package app.seamansbook.tests;

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

import app.seamansbook.tests.adapters.StatisticsListAdapter;
import app.seamansbook.tests.data.QuestionScoresDBManager;
import app.seamansbook.tests.models.QuizResultModel;
import app.seamansbook.tests.models.StatisticsMainInfoModel;

public class StatisticsFragment extends Fragment {

    public StatisticsFragment() {}



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

        successful_questions_count.setText(statisticsMainInfoModel.getSuccessfulTestsCount());
        failed_questions_count.setText(statisticsMainInfoModel.getFailedTestsCount());
        knowledge_level.setText(statisticsMainInfoModel.getKnowledgeLevel());
        startScore.setText(statisticsMainInfoModel.getFirstScore());
        averageScore.setText(statisticsMainInfoModel.getAverageScore());
        bestScore.setText(statisticsMainInfoModel.getBestScore());


        RecyclerView recyclerView = view.findViewById(R.id.statisticsRecyclerView);
        StatisticsListAdapter statisticsListAdapter = new StatisticsListAdapter(quizResultModels, id -> {
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            WrongAnswersFragment detailedStatisticsFragment = new WrongAnswersFragment();
            detailedStatisticsFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, detailedStatisticsFragment).addToBackStack(null).commit();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(statisticsListAdapter);
    }
}