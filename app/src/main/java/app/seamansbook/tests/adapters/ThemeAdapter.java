package app.seamansbook.tests.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.seamansbook.tests.R;
import app.seamansbook.tests.models.ThemeModel;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder> {

    private final List<ThemeModel> themes;
    private int selectedTheme;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClicked(ThemeModel language);
    }

    public ThemeAdapter(List<ThemeModel> themes, int selectedLanguageCode, OnItemClickListener listener) {
        this.themes = themes;
        this.selectedTheme = selectedLanguageCode;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_button_radio, parent, false);
        return new ThemeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {
        ThemeModel theme = themes.get(position);
        holder.radioButton.setChecked(theme.getCode() == selectedTheme);
        holder.radioButton.setText(theme.getName());

        holder.radioButton.setOnClickListener(v -> {
            if(!(theme.getCode() == selectedTheme)){
                selectedTheme = theme.getCode();
                listener.onItemClicked(theme);
            }
        });
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    static class ThemeViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;

        ThemeViewHolder(View view) {
            super(view);
            radioButton = view.findViewById(R.id.radioButtonAnswer);
        }
    }
}
