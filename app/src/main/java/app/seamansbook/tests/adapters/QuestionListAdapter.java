package app.seamansbook.tests.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.seamansbook.tests.R;
import app.seamansbook.tests.interfaces.OnButtonClickListener;
import app.seamansbook.tests.models.QuestionButton;
import app.seamansbook.tests.viewHolders.RadioButtonViewHolder;
import app.seamansbook.tests.viewHolders.RadioButtonViewHolder2;

public class QuestionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int BUTTON_TYPE_1 = 1;
    public static final int BUTTON_TYPE_2 = 2;
    private List<QuestionButton> buttonTypes;
    private final OnButtonClickListener onButtonClickListener;
    Context context;

    public QuestionListAdapter(Context context, List<QuestionButton> buttonTypes, OnButtonClickListener OnButtonClickListener) {
        this.buttonTypes = buttonTypes;
        this.onButtonClickListener = OnButtonClickListener;
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == BUTTON_TYPE_1) {
            View view = inflater.inflate(R.layout.answer_button_radio, parent, false);
            return new RadioButtonViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.answer_button_checkbox, parent, false);
            return new RadioButtonViewHolder2(view);
        }
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        QuestionButton buttonType = buttonTypes.get(position);
        ColorStateList defaultButtonStyle = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{-android.R.attr.state_checked}
                },
                new int[]{
                        Color.parseColor("#00CB47"),
                        Color.parseColor("#606060"),
                }
        );

        RadioButton button;

        if (buttonType.getType() == BUTTON_TYPE_1) {
            button = ((RadioButtonViewHolder) holder).button;
        } else {
            button = ((RadioButtonViewHolder2) holder).button;
        }

        button.setText(buttonType.getText());
        button.setCompoundDrawableTintList(defaultButtonStyle);


        button.setSelected(false);
        button.setChecked(false);
        button.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        button.setBackground(ContextCompat.getDrawable(context, R.drawable.round_back_white_stroke2_10));

        button.setOnClickListener(view -> onButtonClickListener.onButtonClick(buttonType, position, view));
    }


    @Override
    public int getItemCount() {
        return buttonTypes.size();
    }

    @Override
    public int getItemViewType(int position) {
        QuestionButton buttonType = buttonTypes.get(position);
        return (buttonType.getType() == BUTTON_TYPE_1) ? BUTTON_TYPE_1 : BUTTON_TYPE_2;
//        return buttonTypes.get(position).getType();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<QuestionButton> newData) {
        buttonTypes.clear();

        this.buttonTypes = newData;
        notifyDataSetChanged();

    }
}
