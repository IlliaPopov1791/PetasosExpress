package ca.hermeslogistics.itservices.petasosexpress;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SearchScreen extends Fragment {

    private ListView searchStuffListView;
    private ArrayAdapter<String> adapter;
    private List<String> originalItems;
    private EditText searchEditText;

    public SearchScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_screen, container, false);

        searchStuffListView = view.findViewById(R.id.search_stuff);
        searchEditText = view.findViewById(R.id.searchEditText);

        originalItems = Arrays.asList(getResources().getStringArray(R.array.my_stuff));

        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                new ArrayList<>(originalItems)
        );

        searchStuffListView.setAdapter(adapter);

        // Add a TextWatcher to listen for changes in the EditText
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Get the category from the arguments
                Bundle args = getArguments();
                String category = (args != null && args.containsKey("category")) ? args.getString("category") : null;

                // Filter the adapter based on the query text and category
                filterAdapterAndCategory(charSequence.toString(), category);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Set an OnEditorActionListener for the searchEditText
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Handle the search action here
                String query = searchEditText.getText().toString().trim();
                filterAdapterAndCategory(query, null);
                // Close the keyboard
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        // Check if category is provided in arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("category")) {
            String category = args.getString("category");
            // Perform initial search based on the category
            filterAdapterAndCategory("", category);
        }

        return view;
    }

    private void filterAdapterAndCategory(String query, String category) {
        query = query.toLowerCase(Locale.getDefault());
        adapter.clear();

        if (query.length() == 0) {
            // No query, show all items in the selected category
            for (String item : originalItems) {
                if (isItemInCategory(item, category)) {
                    adapter.add(item);
                }
            }
        } else {
            // Filter based on both query and category
            for (String item : originalItems) {
                if (item.toLowerCase(Locale.getDefault()).contains(query) && isItemInCategory(item, category)) {
                    adapter.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private boolean isItemInCategory(String item, String category) {
        if (category == null || category.isEmpty()) {
            return true; // No specific category, always include the item
        }

        item = item.toLowerCase(Locale.getDefault()); // Convert item to lowercase for case-insensitive comparison

        String[] foodItems = {"pizza hut", "kfc", "mcdonald's", "starbucks"};
        String[] electronicsItems = {"best buy", "staples"};
        String[] medicalItems = {"shoppers drug mart", "circle k"};
        String[] otherItems = {"walmart", "bed bath & beyond", "fortinos", "humber bookstore", "costco"};

        String[] selectedItems;

        switch (category.toLowerCase(Locale.getDefault())) {
            case "food":
                selectedItems = foodItems;
                break;
            case "electronics":
                selectedItems = electronicsItems;
                break;
            case "medical":
                selectedItems = medicalItems;
                break;
            case "other":
                selectedItems = otherItems;
                break;
            default:
                return false; // Unknown category
        }

        // Check if the item belongs to the selected category
        for (String selectedItem : selectedItems) {
            if (item.contains(selectedItem)) {
                return true;
            }
        }

        return false;
    }
}