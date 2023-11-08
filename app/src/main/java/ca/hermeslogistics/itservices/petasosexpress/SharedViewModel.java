package ca.hermeslogistics.itservices.petasosexpress;

/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }
}
