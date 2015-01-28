package com.izv.inmoviliariacv;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by rober on 25/11/2014.
 */
public class Ayudante extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bdinmobiliaria.sqlite";
    public static final int DATABASE_VERSION = 1;

    public Ayudante(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.v("aaaaaaaaaaaaaaaaaaaaaaaa", "Constructor ayudante");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql;
        sql = "create table " + Contrato.TablaInmueble.TABLA +
                " (" + Contrato.TablaInmueble._ID+
                " integer primary key autoincrement, "+
                Contrato.TablaInmueble.LOCALIDAD+" text, "+
                Contrato.TablaInmueble.DIRECCION+" text, "+
                Contrato.TablaInmueble.TIPO+" text, "+
                Contrato.TablaInmueble.PRECIO+" real, "+
                Contrato.TablaInmueble.SUBIDO+" integer default 0)";
        Log.v("aaaaaaaaa", sql);
        db.execSQL(sql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //transformar esquemas
        //de la version old a la version new
        //sin que se produzcan pérdidas de datos
        //1º crear tablas de respaldo (identicas)
        //2º copio los datos a esas tablas
        //3º borro las tablas originales
        //4º creo las tablas nuevas(llamo a oncreate)
        //5º copio los datos de las tablas de respaldo en las nuevas tablas
        //6º borro las tablas de respaldo
        String sql = "drop table if exists " + Contrato.TablaInmueble.TABLA;
        db.execSQL(sql);
        onCreate(db);
    }
}