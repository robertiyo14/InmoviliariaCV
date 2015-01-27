package com.izv.inmoviliariacv;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.izv.inmobiliariacv.R;


/**
 * Created by rober on 27/11/2014.
 */
public class Adaptador extends CursorAdapter{

    private Cursor c;

    public Adaptador(Context context, Cursor c){
        super(context,c,true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        View v = i.inflate(R.layout.detalle,parent,false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh;
        Inmueble i=null;
        if(view!=null){
            Inmobiliaria in = new Inmobiliaria(context);
            i = in.getRow(cursor);
            vh = new ViewHolder();
            vh.tvDireccion = (TextView)view.findViewById(R.id.tvDireccion);
            vh.tvLocalidad = (TextView)view.findViewById(R.id.tvLocalidad);
            vh.tvTipo = (TextView)view.findViewById(R.id.tvTipo);
            vh.tvPrecio = (TextView)view.findViewById(R.id.tvPrecio);
            view.setTag(vh);
        }else{
            vh = (ViewHolder)view.getTag();
        }
        vh.tvDireccion.setText(i.getDireccion());
        vh.tvLocalidad.setText(i.getLocalidad());
        vh.tvTipo.setText(i.getTipo());
        vh.tvPrecio.setText(i.getPrecio()+"");
    }

    public static class ViewHolder{
        public TextView tvDireccion, tvLocalidad, tvPrecio, tvTipo;
        public int posicion;
    }
}
