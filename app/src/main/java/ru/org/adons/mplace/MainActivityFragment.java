package ru.org.adons.mplace;

import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends ListFragment {

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("No phone numbers");

//        String[] items = new String[50];
//        for (int i = 0; i < items.length; i++) {
//            items[i] = "item" + i;
//        }
//        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items));
        //setListShown(false);
    }
}
