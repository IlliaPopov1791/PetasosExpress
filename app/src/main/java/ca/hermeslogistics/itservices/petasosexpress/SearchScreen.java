package ca.hermeslogistics.itservices.petasosexpress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class SearchScreen extends Fragment {

    public SearchScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the appropriate layout
        return inflater.inflate(R.layout.fragment_search_screen, container, false);
    }
}
