package ca.hermeslogistics.itservices.petasosexpress;

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
/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class Home extends Fragment {

    private EditText searchEditText;
    private Button placeOrderButton;

    public Home() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI elements
        searchEditText = view.findViewById(R.id.searchEditText);
        placeOrderButton = view.findViewById(R.id.placeOrderButton);

        // Set click listener for the search button
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle search functionality here
                String searchQuery = searchEditText.getText().toString();
                // Perform search based on searchQuery

                Toast.makeText(getActivity(), "Searching for: " + searchQuery, Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for the place order button
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle place order functionality here
                // For example, you can open a new activity or fragment to place an order
                Toast.makeText(getActivity(), "Placing order...", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
