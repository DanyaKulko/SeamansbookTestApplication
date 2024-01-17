package app.seamansbook.tests.viewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.seamansbook.tests.R;

public class FavoritesViewHolder extends RecyclerView.ViewHolder {
    public TextView question_content;
    public TextView answer_content;
    public TextView question_id;
    public ImageView favorite_button;

    public FavoritesViewHolder(@NonNull View itemView) {
        super(itemView);
        question_content = itemView.findViewById(R.id.question_content);
        answer_content = itemView.findViewById(R.id.answer_content);
        question_id = itemView.findViewById(R.id.question_id);
        favorite_button = itemView.findViewById(R.id.favorite_button);
    }
}
