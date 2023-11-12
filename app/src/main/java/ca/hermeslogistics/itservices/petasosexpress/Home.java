package ca.hermeslogistics.itservices.petasosexpress;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class Home extends Fragment {

    private EditText searchEditText;
    private SharedViewModel sharedViewModel;

    public Home() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;

        // Set the layout based on the orientation
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_home_landscape, container, false);
        }

        // Initialize UI elements
        searchEditText = view.findViewById(R.id.searchEditText);

        // Get the SharedViewModel instance
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Set up editor action listener for the search EditText (handle "Enter" key event)
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    // Handle the "Enter" key event by triggering the search action
                    handleSearch();
                    return true;
                }
                return false;
            }
        });

        // Observe changes to the search query
        sharedViewModel.getSearchQuery().observe(getViewLifecycleOwner(), searchQuery -> {
            // Update the UI or perform any action based on the new search query value
            searchEditText.setText(searchQuery);
        });

        return view;
    }

    private void handleSearch() {
        String searchQuery = searchEditText.getText().toString();
        sharedViewModel.setSearchQuery(searchQuery);

        // Create the SearchScreen fragment
        SearchScreen searchScreenFragment = new SearchScreen();

        Bundle args = new Bundle();
        args.putString("searchQuery", searchQuery);
        searchScreenFragment.setArguments(args);
        // Replace the current fragment with the SearchScreen fragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main_frame_layout, searchScreenFragment)
                .addToBackStack(null)
                .commit();

        // Perform search based on searchQuery (optional)
        String message = getString(R.string.searching_for, searchQuery);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
