package app.seamansbook.tests.viewHolders;

import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.seamansbook.tests.R;

public class RadioButtonViewHolder2 extends RecyclerView.ViewHolder {
    public RadioButton button;

    public RadioButtonViewHolder2(@NonNull View itemView) {
        super(itemView);
        button = itemView.findViewById(R.id.checkboxButtonAnswer);
    }
}
