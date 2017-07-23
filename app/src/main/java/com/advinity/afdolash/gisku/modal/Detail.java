package com.advinity.afdolash.gisku.modal;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Afdolash on 7/18/2017.
 */

public class Detail {
    String name;
    LatLng latLng;

    public Detail(String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
