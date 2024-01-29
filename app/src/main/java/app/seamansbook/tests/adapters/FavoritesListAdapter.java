package app.seamansbook.tests.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.seamansbook.tests.R;
import app.seamansbook.tests.models.Question;
import app.seamansbook.tests.viewHolders.FavoritesViewHolder;

public class FavoritesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Question> questions;
    private final Set<String> favoriteQuestionsIds;
    private final SharedPreferences sharedPreferences;

    public FavoritesListAdapter(Context context, List<Question> questions) {
        this.questions = questions;
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences("seamansbookMain", Context.MODE_PRIVATE);
        this.favoriteQuestionsIds = new HashSet<>(sharedPreferences.getStringSet("favorite_questions", new HashSet<>()));
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_favorite_question, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Question question = questions.get(position);
        FavoritesViewHolder favoritesViewHolder = (FavoritesViewHolder) holder;

        List<Integer> correctAnswersIndexes = question.getCorrectAnswersIndexes();
        List<String> answers = question.getAnswers();
        StringBuilder correctAnswer = new StringBuilder();

        for (int i = 0; i < correctAnswersIndexes.size(); i++) {
            int correctAnswerIndex = correctAnswersIndexes.get(i);
            correctAnswer.append(answers.get(correctAnswerIndex)).append(" ");
        }

        favoritesViewHolder.question_content.setText(question.getQuestion());
        favoritesViewHolder.answer_content.setText(correctAnswer.toString());
        favoritesViewHolder.question_id.setText(question.get_id());

        ImageView favoriteBtn = favoritesViewHolder.favorite_button;
        favoriteBtn.setOnClickListener(v -> {
            if (favoriteQuestionsIds.contains(question.get_id())) {
                favoriteQuestionsIds.remove(question.get_id());
                favoriteBtn.setImageResource(R.drawable.icon__star);
            } else {
                favoriteQuestionsIds.add(question.get_id());
                favoriteBtn.setImageResource(R.drawable.icon__favorite_button_filled);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("favorite_questions", new HashSet<>(favoriteQuestionsIds));
            editor.apply();
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
