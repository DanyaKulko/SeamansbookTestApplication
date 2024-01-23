package app.seamansbook.tests.viewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.seamansbook.tests.R;

public class WrongAnswersViewHolder extends RecyclerView.ViewHolder {
    public TextView question_content;
    public TextView wrong_answer_content;
    public TextView correct_answer_content;
    public ImageView favorite_button;

    public WrongAnswersViewHolder(@NonNull View itemView) {
        super(itemView);
        question_content = itemView.findViewById(R.id.question_content);
        wrong_answer_content = itemView.findViewById(R.id.wrong_answer_content);
        correct_answer_content = itemView.findViewById(R.id.answer_content);
        favorite_button = itemView.findViewById(R.id.favorite_button);
    }
}
