package ca.hermeslogistics.itservices.petasosexpress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class SpinnerAdapter extends ArrayAdapter<String> {
    private LayoutInflater mInflater;

    public SpinnerAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.spinner_ui, parent, false);
        TextView textView = convertView.findViewById(R.id.spinner_item);
        textView.setText(getItem(position));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            return new View(getContext());
        }
        return super.getDropDownView(position, null, parent);
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }
}
