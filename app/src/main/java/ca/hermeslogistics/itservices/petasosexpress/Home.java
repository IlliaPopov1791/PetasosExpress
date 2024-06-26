package ca.hermeslogistics.itservices.petasosexpress;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Home extends Fragment {

    EditText searchEditText;
    SharedViewModel sharedViewModel;

    public Home() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        SharedPreferences settings = getActivity().getSharedPreferences(AppSettings.PREFS_NAME, 0);
        boolean isLandscapePreference = settings.getBoolean(AppSettings.ORIENTATION_KEY, false);

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

        //Food Button
        ImageButton foodButton = view.findViewById(R.id.foodButton);
        foodButton.setOnClickListener(v -> {
            searchEditText.setText(R.string.food_1);
            handleSearch();
        });

        //Electronics Button
        ImageButton electronicsButton = view.findViewById(R.id.electronicsButton);
        electronicsButton.setOnClickListener(v -> {
            searchEditText.setText(R.string.technology_1);
            handleSearch();
        });

        //Medical Button
        ImageButton medicalButton = view.findViewById(R.id.medicalButton);
        medicalButton.setOnClickListener(v -> {
            searchEditText.setText(R.string.medicine);
            handleSearch();
        });

        //Other Button
        ImageButton otherButton = view.findViewById(R.id.otherButton);
        otherButton.setOnClickListener(v -> {
            searchEditText.setText(R.string.other);
            handleSearch();
        });

        FloatingActionButton fabCart = view.findViewById(R.id.fab_cart);
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCartFragment();
            }
        });

        return view;
    }


    void handleSearch() {
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

    void loadCartFragment() {
        CartScreen cartScreenFragment = new CartScreen();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main_frame_layout, cartScreenFragment)
                .addToBackStack(null) // Optional: adds the transaction to the back stack
                .commit();
    }
}


