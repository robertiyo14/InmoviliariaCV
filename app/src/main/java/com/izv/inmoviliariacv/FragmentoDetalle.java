package com.izv.inmoviliariacv;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.izv.inmobiliariacv.R;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentoDetalle extends Fragment {

    TextView tvTituloDetalle;
    ImageView iv;
    View v;

    public FragmentoDetalle() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fragmento_detalle, container, false);
        iv =(ImageView) v.findViewById(R.id.imageView);
        return v;
    }

    public void setText(String s){
        this.tvTituloDetalle = (TextView)v.findViewById(R.id.tvTituloDetalle);
        this.tvTituloDetalle.setText(s);
    }
}
