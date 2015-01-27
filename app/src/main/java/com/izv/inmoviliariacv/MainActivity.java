package com.izv.inmoviliariacv;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.izv.inmobiliariacv.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class MainActivity extends Activity {

    private Inmobiliaria in;
    private Adaptador ad;
    private static final int CREAR=0;
    private static final int MODIFICAR=1;
    private final int DETALLE = 2;
    private final int FOTO = 3;
    ListView lv;
    Inmueble inmuebleFoto;
    ArrayList <File> fotos;
    int posActual;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Activity.RESULT_OK){
            String localidad;
            String direccion;
            String tipo;
            String precio;
            long index=0;
            Inmueble i;
            in.open();
            switch (requestCode){
                case CREAR:
                    //Hago cosas
                    localidad = data.getStringExtra("localidad");
                    direccion = data.getStringExtra("direccion");
                    tipo = data.getStringExtra("tipo");
                    precio = data.getStringExtra("precio");
                    i = new Inmueble(localidad,direccion,tipo,precio);
                    in.insert(i);
                    actualizarLista();
                    break;
                case MODIFICAR:
                    //Hago cosas
                    index = data.getLongExtra("index",-1);
                    localidad = data.getStringExtra("localidad");
                    direccion = data.getStringExtra("direccion");
                    tipo = data.getStringExtra("tipo");
                    precio = data.getStringExtra("precio");
                    Double prec = Double.parseDouble(precio);
                    i = new Inmueble(index,localidad,direccion,tipo,prec);
                    in.update(i);
                    actualizarLista();
                    break;
                case DETALLE:
                    i =(Inmueble) data.getSerializableExtra("inmueble");
                    ArrayList <File>fotos = new ArrayList<File>();
                    FragmentoDetalle fd = (FragmentoDetalle)getFragmentManager().findFragmentById(R.id.fragment3);
                    fotos = (ArrayList)data.getExtras().get("fotos");
                    if(fotos.size()>0){
                        fd.iv.setImageURI(Uri.fromFile(fotos.get(0)));
                    }
                    fd.setText(i.getDireccion() + ", " + i.getLocalidad());
                    break;
                case FOTO:
                    Bitmap foto = (Bitmap)data.getExtras().get("data");
                    FileOutputStream salida;
                    i = inmuebleFoto;
                    Calendar cal = new GregorianCalendar();
                    Date date = cal.getTime();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
                    String formatteDate = df.format(date);
                    try {
                        salida = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_DCIM)
                                +"/inmueble_"+i.getId()+"_"+formatteDate+".jpg");
                        foto.compress(Bitmap.CompressFormat.JPEG, 90, salida);
                    } catch (FileNotFoundException e) {
                    }


                    break;
            }
        }else {
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);
        in = new Inmobiliaria(this);
        lv=(ListView)findViewById(R.id.lvLista);
        registerForContextMenu(lv);
        final ListView lv = (ListView)findViewById(R.id.lvLista);
        final FragmentoDetalle fd = (FragmentoDetalle)getFragmentManager().findFragmentById(R.id.fragment3);
        posActual = 0;


        final boolean horizontal = fd!=null && fd.isInLayout();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v,int pos, long id) {
                List<Inmueble> al = in.select(null,null,null);
                fotos = new ArrayList<File>();
                String ruta = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath();
                File dir = new  File(ruta);
                File[] fotos1 = dir.listFiles();
                for (int i = 0; i < fotos1.length; i++) {
                    String idIn="";
                    idIn=fotos1[i].getName().split("_")[1];
                    Log.v("ID:","ID:"+idIn);
                    if(idIn.equals(al.get(pos).getId()+"")){
                        fotos.add(fotos1[i]);
                    }
                }
                Inmueble inm = al.get(pos);
                if(horizontal){
                    Button btSiguiente = (Button)findViewById(R.id.btSiguiente);
                    Button btAnterior = (Button)findViewById(R.id.btAnterior);
                    btSiguiente.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int posMax;
                            posMax=fotos.size()-1;
                            if(posActual+1<=posMax){
                                fd.iv.setImageURI(Uri.fromFile(fotos.get(posActual+1)));
                                posActual++;
                            }else{
                                fd.iv.setImageURI(Uri.fromFile(fotos.get(0)));
                                posActual=0;
                            }
                        }
                    });
                    btAnterior.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int posMax;
                            posMax=fotos.size()-1;
                            if(posActual-1>=0){
                                fd.iv.setImageURI(Uri.fromFile(fotos.get(posActual-1)));
                                posActual--;
                            }else{
                                fd.iv.setImageURI(Uri.fromFile(fotos.get(posMax)));
                                posActual=posMax;
                            }
                        }
                    });
                    fd.setText(inm.getDireccion()+", "+inm.getLocalidad());
                    if(fotos.size()>0){
                        fd.iv.setImageURI(Uri.fromFile(fotos.get(0)));
                    }

                }else{
                    Intent i = new Intent(MainActivity.this,Fotos.class);
                    i.putExtra("inmueble",inm);
                    i.putExtra("fotos", fotos);
                    startActivityForResult(i, DETALLE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        in.open();
        Cursor c = in.getCursor(null, null, null);
        ad = new Adaptador(this, c);
        lv.setAdapter(ad);
        //datosPrueba();
    }

    @Override
    protected void onPause() {
        super.onPause();
        in.close();
    }

    public void datosPrueba(){
        String[] direcciones = {"Dir 1","Dir 2","Dir 3"};
        String[] tipos = {"Adosado", "Apartamento", "Chalet"};
        String[] localidades = {"Alhendin","Armilla","Otura"};
        String[] precios = {"60000.50","55000.00","100000.30"};
        for (int i = 0; i < direcciones.length; i++) {
            Inmueble inmueble = new Inmueble(direcciones[i],tipos[i],localidades[i],precios[i]);
            Long id = in.insert(inmueble);
        }
        Cursor c = in.getCursor(null,null,null);
        ad.changeCursor(c);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_new:
                //nuevoInmueble();
                Intent i = new Intent(this,Anadir.class);
                startActivityForResult(i, CREAR);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.long_clic, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int id=item.getItemId();
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int index = info.position;
        List<Inmueble> al = in.select(null,null,null);
        Inmueble i = al.get(index);
        if(id==R.id.action_borrar){
            in.delete(i);
            Cursor c = in.getCursor(null,null,null);
            ad.changeCursor(c);
        }else if(id==R.id.action_modificar){
            Intent intent = new Intent(this,Anadir.class);
            Bundle b = new Bundle();
            b.putSerializable("inmueble", i);
            b.putInt("index", index);
            intent.putExtras(b);
            startActivityForResult(intent, MODIFICAR);
        }else if (id==R.id.action_foto){
            inmuebleFoto = i;
            Intent intent = new Intent ("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, FOTO);
        }
        return super.onContextItemSelected(item);
    }

    public void actualizarLista(){
        Cursor c = in.getCursor(null,null,null);
        ad.changeCursor(c);
    }



}