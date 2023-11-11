package ca.hermeslogistics.itservices.petasosexpress;
/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchResultViewHolder extends RecyclerView.ViewHolder {

    private TextView nameTextView;
    private TextView addressTextView;
    private TextView typeTextView;

    public SearchResultViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.nameTextView);
        addressTextView = itemView.findViewById(R.id.addressTextView);
        typeTextView = itemView.findViewById(R.id.typeTextView);
    }

    public void bind(SearchResult searchResult) {
        nameTextView.setText(searchResult.getName());
        addressTextView.setText(searchResult.getAddress());
        typeTextView.setText(searchResult.getType());
    }
}