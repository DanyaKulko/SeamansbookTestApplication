package app.seamansbook.tests;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TopSheetNotificationsFragment extends BottomSheetDialogFragment {
//    @Override
//    public int getTheme() {
//        return R.style.CustomBottomSheetDialog;
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        // Создание View для BottomSheet из layout файла
        View view = View.inflate(getContext(), R.layout.fragment_top_sheet_notifications, null);
        dialog.setContentView(view);

        // Настройка поведения
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
//
//        // Настройка начальной позиции
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//
//        // Настройка направления анимации
        behavior.setHideable(false);
//
//        // Настройка гравитации
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        params.gravity = Gravity.TOP;
        ((View) view.getParent()).setLayoutParams(params);

        // Настройка анимации
//        ((View) view.getParent()).setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_top));

        return dialog;
    }
}
