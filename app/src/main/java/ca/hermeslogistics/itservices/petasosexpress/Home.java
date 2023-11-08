package ca.hermeslogistics.itservices.petasosexpress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ca.hermeslogistics.itservices.petasosexpress.SharedViewModel;

public class Home extends Fragment {

    private EditText searchEditText;
    private SharedViewModel sharedViewModel;

    public Home() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI elements
        searchEditText = view.findViewById(R.id.searchEditText);

        // Get the SharedViewModel instance
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Set up click listener for the search button
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchEditText.getText().toString();
                // Update the search query in the SharedViewModel
                sharedViewModel.setSearchQuery(searchQuery);

                // Create the SearchScreen fragment
                SearchScreen searchScreenFragment = new SearchScreen();

                // Replace the current fragment with the SearchScreen fragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, searchScreenFragment) // R.id.fragment_container is the container in your activity layout where you want to replace the fragments
                        .addToBackStack(null) // Optional: Allows the user to navigate back to the previous fragment
                        .commit();

                // Perform search based on searchQuery (optional)
                String message = getString(R.string.searching_for, searchQuery);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe changes to the search query
        sharedViewModel.getSearchQuery().observe(getViewLifecycleOwner(), searchQuery -> {
            // Update the UI or perform any action based on the new search query value
            searchEditText.setText(searchQuery);
        });

        return view;
    }
}
