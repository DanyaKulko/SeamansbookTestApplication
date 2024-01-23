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
import app.seamansbook.tests.models.WrongAnswersModel;
import app.seamansbook.tests.viewHolders.WrongAnswersViewHolder;

public class WrongAnswersListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final List<WrongAnswersModel> questions;
    private final Set<String> favoriteQuestionsIds;
    private final SharedPreferences sharedPreferences;


    public WrongAnswersListAdapter(Context context, List<WrongAnswersModel> questions) {
        this.questions = questions;
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences("seamansbookMain", Context.MODE_PRIVATE);
        this.favoriteQuestionsIds = new HashSet<>(sharedPreferences.getStringSet("favorite_questions", new HashSet<>()));
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_wrong_answer, parent, false);
        return new WrongAnswersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WrongAnswersModel question = questions.get(position);
        WrongAnswersViewHolder wrongAnswersViewHolder = (WrongAnswersViewHolder) holder;
        wrongAnswersViewHolder.question_content.setText(question.getQuestion());
        wrongAnswersViewHolder.wrong_answer_content.setText(String.join(", ", question.getUserAnswer()));
        wrongAnswersViewHolder.correct_answer_content.setText(String.join(", ", question.getCorrectAnswer()));


        ImageView favoriteBtn = wrongAnswersViewHolder.favorite_button;
        updateFavoriteButton(favoriteBtn, question.getId());

        favoriteBtn.setOnClickListener(v -> {
            if (favoriteQuestionsIds.contains(question.getId())) {
                favoriteQuestionsIds.remove(question.getId());
            } else {
                favoriteQuestionsIds.add(question.getId());
            }
            updateFavoriteButton(favoriteBtn, question.getId());

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("favorite_questions", new HashSet<>(favoriteQuestionsIds));
            editor.apply();
        });
    }

    private void updateFavoriteButton(ImageView favoriteBtn, String questionId) {
        if (favoriteQuestionsIds.contains(questionId)) {
            favoriteBtn.setImageResource(R.drawable.favorite_button_field);
        } else {
            favoriteBtn.setImageResource(R.drawable.star_icon);
        }
    }

    @Override
    public int getItemCount() {
        return this.questions.size();
    }
}
