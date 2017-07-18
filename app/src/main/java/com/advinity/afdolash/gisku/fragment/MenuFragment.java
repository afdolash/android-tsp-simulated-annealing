package com.advinity.afdolash.gisku.fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.advinity.afdolash.gisku.R;
import com.advinity.afdolash.gisku.activity.MainActivity;
import com.advinity.afdolash.gisku.sa.City;
import com.advinity.afdolash.gisku.sa.Tour;
import com.advinity.afdolash.gisku.sa.TourManager;
import com.advinity.afdolash.gisku.sa.Utility;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    private Button btn_solve, btn_reload, btn_detail, btn_random;
    private EditText et_temp, et_coolingRate, et_absZero, et_random, et_distance;
    private ProgressDialog progressDialog;

    // Declaration simulated annealing variables
    double temp;
    double coolingRate;
    double absoluteZero;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Initialize widget
        btn_solve = (Button) view.findViewById(R.id.btn_solve);
        btn_reload = (Button) view.findViewById(R.id.btn_reload);
        btn_detail = (Button) view.findViewById(R.id.btn_detail);
        btn_random = (Button) view.findViewById(R.id.btn_random);

        et_absZero = (EditText) view.findViewById(R.id.et_abszero);
        et_coolingRate = (EditText) view.findViewById(R.id.et_coolrate);
        et_distance = (EditText) view.findViewById(R.id.et_distance);
        et_random = (EditText) view.findViewById(R.id.et_random);
        et_temp = (EditText) view.findViewById(R.id.et_temp);

        // Progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Find shortest distance...");

        // Widget event
        btn_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countRandom = Integer.parseInt(et_random.getText().toString());

                if (countRandom > 9 || (TourManager.numberOfCities() + countRandom) > 9) {
                    Toast.makeText(getActivity().getApplicationContext(), "Cant load more than 9 points", Toast.LENGTH_SHORT);
                    return;
                } else {
                    ((MainActivity) getActivity()).getRandomLocation(Integer.parseInt(et_random.getText().toString()));

                    getActivity().getFragmentManager().popBackStack();
                }
            }
        });

        btn_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).markerPoints = new ArrayList<LatLng>();
                ((MainActivity) getActivity()).mMap.clear();
                TourManager.clearTour();

                getActivity().getFragmentManager().popBackStack();
            }
        });

        btn_solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TourManager.numberOfCities() < 3) {
                    Toast.makeText(getActivity().getApplicationContext(), "Set minimum 3 points", Toast.LENGTH_SHORT);
                    return;
                } else if (TourManager.numberOfCities() > 9) {
                    Toast.makeText(getActivity().getApplicationContext(), "Cant load more than 9 points", Toast.LENGTH_SHORT);
                    return;
                }

                // Progress dialog
                progressDialog.show();

                // Initialize simulated annealing variables
                temp = Double.parseDouble(et_temp.getText().toString());
                coolingRate = Double.parseDouble(et_coolingRate.getText().toString());
                absoluteZero = Double.parseDouble(et_absZero.getText().toString());

                //create random intial solution
                Tour currentSolution = new Tour();
                currentSolution.generateIndividual();

                Toast.makeText(getActivity(), "Total distance : "+ currentSolution.getTotalDistance(), Toast.LENGTH_SHORT).show();

                // We would like to keep track if the best solution
                // Assume best solution is the current solution
                Tour best = new Tour(currentSolution.getTour());

                // Loop until system has cooled
                while (temp > absoluteZero) {
                    // Create new neighbour tour
                    Tour newSolution = new Tour(currentSolution.getTour());

                    // Get random positions in the tour
                    int tourPos1 = Utility.randomInt(0 , newSolution.tourSize());
                    int tourPos2 = Utility.randomInt(0 , newSolution.tourSize());

                    //to make sure that tourPos1 and tourPos2 are different
                    while(tourPos1 == tourPos2) {tourPos2 = Utility.randomInt(0 , newSolution.tourSize());}

                    // Get the cities at selected positions in the tour
                    City citySwap1 = newSolution.getCity(tourPos1);
                    City citySwap2 = newSolution.getCity(tourPos2);

                    // Swap them
                    newSolution.setCity(tourPos2, citySwap1);
                    newSolution.setCity(tourPos1, citySwap2);

                    // Get energy of solutions
                    double currentDistance   = currentSolution.getTotalDistance();
                    double neighbourDistance = newSolution.getTotalDistance();

                    // Decide if we should accept the neighbour
                    double rand = Utility.randomDouble();
                    if (Utility.acceptanceProbability(currentDistance, neighbourDistance, temp) > rand) {
                        currentSolution = new Tour(newSolution.getTour());
                    }

                    // Keep track of the best solution found
                    if (currentSolution.getTotalDistance() < best.getTotalDistance()) {
                        best = new Tour(currentSolution.getTour());
                    }

                    // Cool system
                    temp *= coolingRate;
                }

                Toast.makeText(getActivity(), "Final distance : " + best.getTotalDistance(), Toast.LENGTH_SHORT).show();

                progressDialog.hide();

                ((MainActivity) getActivity()).getWaypoints(best);

                getActivity().getFragmentManager().popBackStack();

            }
        });

        return view;
    }

}
