package ca.hermeslogistics.itservices.petasosexpress;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Home extends Fragment {

    private EditText searchEditText;

    public Home() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Load landscape layout
            view = inflater.inflate(R.layout.home_landscape, container, false);
        } else {
            // Load portrait layout
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }

        // Initialize UI elements
        searchEditText = view.findViewById(R.id.searchEditText);

        // Set click listener for the search button
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchEditText.getText().toString();
                // Perform search based on searchQuery
                String message = getString(R.string.searching_for, searchQuery);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
