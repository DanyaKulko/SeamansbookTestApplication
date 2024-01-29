package app.seamansbook.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Objects;

import app.seamansbook.tests.adapters.WrongAnswersListAdapter;
import app.seamansbook.tests.data.QuestionScoresDBManager;
import app.seamansbook.tests.models.WrongAnswersFragmentModel;

public class WrongAnswersFragment extends Fragment {

    private String id = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wrong_answers, container, false);

        if(getArguments() != null) {
            id = getArguments().getString("id");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(id == null) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        QuestionScoresDBManager scoresDBManager = new QuestionScoresDBManager(requireContext());
        WrongAnswersFragmentModel quizResultModels = scoresDBManager.getWrongAnswers(id);

        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("selected_language", "null");
        Locale locale = Locale.getDefault();

        if (!Objects.equals(language, "null")) {
            locale = new Locale(language);
        }

        TextView quizDateTextView = view.findViewById(R.id.quizDateTextView);
        quizDateTextView.setText(quizResultModels.getDate(locale));

        RecyclerView recyclerView = view.findViewById(R.id.wrongAnswersRecyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        WrongAnswersListAdapter wrongAnswersAdapter = new WrongAnswersListAdapter(requireContext(), quizResultModels.getWrongAnswers());
        recyclerView.setAdapter(wrongAnswersAdapter);
    }
}