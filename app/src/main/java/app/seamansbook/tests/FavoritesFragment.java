package app.seamansbook.tests;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.seamansbook.tests.adapters.FavoritesListAdapter;
import app.seamansbook.tests.data.QuestionDBManager;
import app.seamansbook.tests.models.Question;


public class FavoritesFragment extends Fragment {

    public FavoritesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.favoritesRecyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("seamansbookMain", MODE_PRIVATE);
        Set<String> favoriteQuestionsIds = sharedPreferences.getStringSet("favorite_questions", new HashSet<>());

        if (!favoriteQuestionsIds.isEmpty()) {
            QuestionDBManager dbManager = new QuestionDBManager(requireContext());
            List<Question> favoriteQuestions = dbManager.getQuestionsByIds(new ArrayList<>(favoriteQuestionsIds));
            FavoritesListAdapter favoritesListAdapter = new FavoritesListAdapter(requireContext(), favoriteQuestions);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            recyclerView.setAdapter(favoritesListAdapter);
        }
    }
}