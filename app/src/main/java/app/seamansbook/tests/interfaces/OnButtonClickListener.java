package app.seamansbook.tests.interfaces;

import android.view.View;

import app.seamansbook.tests.models.QuestionButton;

public interface OnButtonClickListener {

    void onButtonClick(QuestionButton buttonType, int position, View view);
}
