package app.seamansbook.tests.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.seamansbook.tests.R;
import app.seamansbook.tests.models.LanguageModel;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private final List<LanguageModel> languages;
    private String selectedLanguageCode;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClicked(LanguageModel language);
    }

    public LanguageAdapter(List<LanguageModel> languages, String selectedLanguageCode, OnItemClickListener listener) {
        this.languages = languages;
        this.selectedLanguageCode = selectedLanguageCode;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_button_radio, parent, false);
        return new LanguageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        LanguageModel language = languages.get(position);
        holder.radioButton.setChecked(language.getCode().equals(selectedLanguageCode));
        holder.radioButton.setText(language.getName());

        holder.radioButton.setOnClickListener(v -> {
            if(!language.getCode().equals(selectedLanguageCode)){
//                int prevIndex = getSelectedLanguageIndex();
                selectedLanguageCode = language.getCode();
//                notifyItemChanged(prevIndex);
//                notifyItemChanged(position);
                listener.onItemClicked(language);
            }
        });
    }
//    private int getSelectedLanguageIndex() {
//        for (int i = 0; i < languages.size(); i++) {
//            if (languages.get(i).getCode().equals(selectedLanguageCode)) {
//                return i;
//            }
//        }
//        return -1; // если ничего не найдено
//    }
    @Override
    public int getItemCount() {
        return languages.size();
    }

    static class LanguageViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;

        LanguageViewHolder(View view) {
            super(view);
            radioButton = view.findViewById(R.id.radioButtonAnswer);
        }
    }
}
