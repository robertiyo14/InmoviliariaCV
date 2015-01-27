package com.izv.inmoviliariacv;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rober on 25/11/2014.
 */
public class Contrato {

    private Contrato (){
    }

    public static abstract class TablaInmueble implements BaseColumns {
        public static final String TABLA = "Inmueble";
        public static final String LOCALIDAD = "localidad";
        public static final String DIRECCION = "direccion";
        public static final String TIPO = "tipo";
        public static final String PRECIO = "precio";
        public static final String SUBIDO = "subido";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.izv.inmobiliaria";
        public static final Uri CONTENT_URI =Uri.parse("content://" + Proveedor.AUTORIDAD + "/" + TABLA);
    }

}

