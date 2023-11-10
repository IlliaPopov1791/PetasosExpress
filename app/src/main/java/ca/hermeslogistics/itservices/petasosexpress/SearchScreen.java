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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchScreen extends Fragment {

    private ListView searchStuffListView;
    private ArrayAdapter<String> adapter;
    private List<String> fullItemList;
    private EditText searchEditText;
    private FirebaseFirestore db;

    public SearchScreen() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_screen, container, false);

        searchStuffListView = view.findViewById(R.id.search_stuff);
        searchEditText = view.findViewById(R.id.searchEditText);
        db = FirebaseFirestore.getInstance();

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        searchStuffListView.setAdapter(adapter);

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
                fullItemList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    Double price = document.getDouble("price");

                    if (name != null && price != null) {
                        String formattedItem = name + " - $" + String.format(Locale.getDefault(), "%.2f", price);
                        fullItemList.add(formattedItem);
                    }
                }
                adapter.addAll(fullItemList);
                adapter.notifyDataSetChanged();

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
        List<String> filteredList = new ArrayList<>();

        for (String item : fullItemList) {
            if (item.toLowerCase(Locale.getDefault()).contains(query)) {
                filteredList.add(item);
            }
        }

        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }
}
