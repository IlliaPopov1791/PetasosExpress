package ca.hermeslogistics.itservices.petasosexpress;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */

public class SearchScreen extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public SearchScreen() {
        // Required empty public constructor
    }

    public static SearchScreen newInstance(String param1, String param2) {
        SearchScreen fragment = new SearchScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Determine the device's current orientation
        int orientation = getResources().getConfiguration().orientation;

        // Inflate the appropriate layout based on orientation
        View view;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view = inflater.inflate(R.layout.search_screen_landscape, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_search_screen, container, false);
        }

        return view;
    }
}
