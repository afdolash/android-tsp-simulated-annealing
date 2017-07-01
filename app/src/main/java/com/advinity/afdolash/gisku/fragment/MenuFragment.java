package com.advinity.afdolash.gisku.fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.advinity.afdolash.gisku.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    private Button btn_solve;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Initialize widget
        btn_solve = (Button) view.findViewById(R.id.btn_solve);

        // Widget event
        btn_solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        return view;
    }

}
