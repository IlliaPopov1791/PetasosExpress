package ca.hermeslogistics.itservices.petasosexpress;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchScreen extends Fragment {

    private ListView searchResultsListView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> fullItemList; // Full list of products
    private EditText searchEditText;
    private FirebaseFirestore db;

    public SearchScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_screen, container, false);

        searchResultsListView = view.findViewById(R.id.search_stuff);
        searchEditText = view.findViewById(R.id.searchEditText);
        db = FirebaseFirestore.getInstance();

        productList = new ArrayList<>();
        fullItemList = new ArrayList<>();
        productAdapter = new ProductAdapter(requireContext(), productList);
        searchResultsListView.setAdapter(productAdapter);

        Bundle args = getArguments();
        String initialQuery = args != null ? args.getString("searchQuery", "") : "";
        searchEditText.setText(initialQuery);
        fetchItems(initialQuery);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterAdapter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString().trim();
                filterAdapter(query);
                closeKeyboard();
                return true;
            }
            return false;
        });

        return view;
    }

    private void fetchItems(String initialQuery) {
        db.collection("goods").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                fullItemList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    Double price = document.getDouble("price");
                    String producer = document.getString("producer");
                    String type = document.getString("type");

                    if (name != null && price != null) {
                        Product product = new Product(name, price, producer, type);
                        fullItemList.add(product);
                        productList.add(product);
                    }
                }
                productAdapter.notifyDataSetChanged();

                // Filter with the initial query if it exists
                if (!initialQuery.isEmpty()) {
                    filterAdapter(initialQuery);
                }
            } else {
                Log.e("FetchItems", "Error fetching documents", task.getException());
            }
        });
    }

    private void filterAdapter(String query) {
        query = query.toLowerCase(Locale.getDefault());
        List<Product> filteredList = new ArrayList<>();

        for (Product product : fullItemList) { // Filter from the full list
            if (product.matchesQuery(query)) {
                filteredList.add(product);
            }
        }

        productAdapter.clear();
        productAdapter.addAll(filteredList);
        productAdapter.notifyDataSetChanged();
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }
}
