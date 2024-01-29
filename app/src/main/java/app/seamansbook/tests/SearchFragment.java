package app.seamansbook.tests;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SearchFragment extends Fragment {


    public SearchFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView = requireView().findViewById(R.id.telegramGradientTitleTextView);

        Shader textShader = new LinearGradient(0, 0, 0, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#00CB47"),
                        Color.parseColor("#00A3B4"),
                }, null, Shader.TileMode.CLAMP);

        textView.getPaint().setShader(textShader);


        TextView martesthelpHyperlink = requireView().findViewById(R.id.martesthelpHyperlink);
        martesthelpHyperlink.setMovementMethod(LinkMovementMethod.getInstance());
        martesthelpHyperlink.setLinkTextColor(ResourcesCompat.getColor(getResources(), R.color.primaryText, null));

        TextView martestHyperlink = requireView().findViewById(R.id.martestHyperlink);
        martestHyperlink.setMovementMethod(LinkMovementMethod.getInstance());
        martestHyperlink.setLinkTextColor(ResourcesCompat.getColor(getResources(), R.color.primaryText, null));



    }
}