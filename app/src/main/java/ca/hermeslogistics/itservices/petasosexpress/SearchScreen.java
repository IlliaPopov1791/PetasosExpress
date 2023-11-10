package ca.hermeslogistics.itservices.petasosexpress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchScreen extends Fragment {

    private EditText searchEditText;
    private RecyclerView searchResultsRecyclerView;
    private SearchResultAdapter searchResultAdapter;

    public SearchScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_screen, container, false);
        searchEditText = view.findViewById(R.id.searchEditText);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);

        // Initialize the RecyclerView adapter and set it to the RecyclerView
        searchResultAdapter = new SearchResultAdapter();
        searchResultsRecyclerView.setAdapter(searchResultAdapter);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up search functionality
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString();
                // Perform search based on query and update the RecyclerView
                List<SearchResult> searchResults = performSearch(query);
                searchResultAdapter.setSearchResults(searchResults);
                return true;
            }
            return false;
        });

        return view;
    }

    private List<SearchResult> performSearch(String query) {
        List<SearchResult> results = new ArrayList<>();
        results.add(new SearchResult("Place 1", "Address 1", "Type 1"));
        results.add(new SearchResult("Place 2", "Address 2", "Type 2"));
        return results;
    }
}
