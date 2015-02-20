package com.izv.inmoviliariacv;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.izv.inmobiliariacv.R;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class MainActivity extends Activity {

    private Inmobiliaria in;
    private Adaptador ad;
    private static final int CREAR = 0;
    private static final int MODIFICAR = 1;
    private final int DETALLE = 2;
    private final int FOTO = 3;
    private String usuario;
    ListView lv;
    Inmueble inmuebleFoto;
    ArrayList<File> fotos;
    int posActual;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String localidad;
            String direccion;
            String tipo;
            String precio;
            long index = 0;
            Inmueble i;
            switch (requestCode) {
                case CREAR:
                    //Hago cosas
                    localidad = data.getStringExtra("localidad");
                    direccion = data.getStringExtra("direccion");
                    tipo = data.getStringExtra("tipo");
                    precio = data.getStringExtra("precio");
                    i = new Inmueble(localidad, direccion, tipo, precio);
                    in.insert(i);
                    actualizarLista();
                    break;
                case MODIFICAR:
                    //Hago cosas
                    index = data.getLongExtra("index", -1);
                    localidad = data.getStringExtra("localidad");
                    direccion = data.getStringExtra("direccion");
                    tipo = data.getStringExtra("tipo");
                    precio = data.getStringExtra("precio");
                    Double prec = Double.parseDouble(precio);
                    i = new Inmueble(index, localidad, direccion, tipo, prec);
                    in.update(i);
                    actualizarLista();
                    break;
                case DETALLE:
                    i = (Inmueble) data.getSerializableExtra("inmueble");
                    ArrayList<File> fotos = new ArrayList<File>();
                    FragmentoDetalle fd = (FragmentoDetalle) getFragmentManager().findFragmentById(R.id.fragment3);
                    fotos = (ArrayList) data.getExtras().get("fotos");
                    if (fotos.size() > 0) {
                        fd.iv.setImageURI(Uri.fromFile(fotos.get(0)));
                    }
                    fd.setText(i.getDireccion() + ", " + i.getLocalidad());
                    break;
                case FOTO:
                    Bitmap foto = (Bitmap) data.getExtras().get("data");
                    FileOutputStream salida;
                    i = inmuebleFoto;
                    Calendar cal = new GregorianCalendar();
                    Date date = cal.getTime();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
                    String formatteDate = df.format(date);
                    try {
                        salida = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_DCIM)
                                + "/inmueble_" + i.getId() + "_" + formatteDate + ".jpg");
                        foto.compress(Bitmap.CompressFormat.JPEG, 90, salida);
                    } catch (FileNotFoundException e) {
                    }


                    break;
            }
        } else {
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        usuario = sharedPref.getString("usuario", "Rober");
        in = new Inmobiliaria(this);
        Cursor c = in.query();
        ad = new Adaptador(this, c);
        lv = (ListView) findViewById(R.id.lvLista);
        lv.setAdapter(ad);
        registerForContextMenu(lv);
        final ListView lv = (ListView) findViewById(R.id.lvLista);
        final FragmentoDetalle fd = (FragmentoDetalle) getFragmentManager().findFragmentById(R.id.fragment3);
        posActual = 0;
        final boolean horizontal = fd != null && fd.isInLayout();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                List<Inmueble> al = in.select(null, null, null);
                fotos = new ArrayList<File>();
                String ruta = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath();
                File dir = new File(ruta);
                File[] fotos1 = dir.listFiles();
                for (int i = 0; i < fotos1.length; i++) {
                    String idIn = "";
                    idIn = fotos1[i].getName().split("_")[1];
                    Log.v("ID:", "ID:" + idIn);
                    if (idIn.equals(al.get(pos).getId() + "")) {
                        fotos.add(fotos1[i]);
                    }
                }
                Inmueble inm = al.get(pos);
                if (horizontal) {
                    Button btSiguiente = (Button) findViewById(R.id.btSiguiente);
                    Button btAnterior = (Button) findViewById(R.id.btAnterior);
                    btSiguiente.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int posMax;
                            posMax = fotos.size() - 1;
                            if (posActual + 1 <= posMax) {
                                fd.iv.setImageURI(Uri.fromFile(fotos.get(posActual + 1)));
                                posActual++;
                            } else {
                                fd.iv.setImageURI(Uri.fromFile(fotos.get(0)));
                                posActual = 0;
                            }
                        }
                    });
                    btAnterior.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int posMax;
                            posMax = fotos.size() - 1;
                            if (posActual - 1 >= 0) {
                                fd.iv.setImageURI(Uri.fromFile(fotos.get(posActual - 1)));
                                posActual--;
                            } else {
                                fd.iv.setImageURI(Uri.fromFile(fotos.get(posMax)));
                                posActual = posMax;
                            }
                        }
                    });
                    fd.setText(inm.getDireccion() + ", " + inm.getLocalidad());
                    if (fotos.size() > 0) {
                        fd.iv.setImageURI(Uri.fromFile(fotos.get(0)));
                    }

                } else {
                    Intent i = new Intent(MainActivity.this, Fotos.class);
                    i.putExtra("inmueble", inm);
                    i.putExtra("fotos", fotos);
                    startActivityForResult(i, DETALLE);
                }
            }
        });
    }


    public void datosPrueba() {
        String[] direcciones = {"Dir 1", "Dir 2", "Dir 3"};
        String[] tipos = {"Adosado", "Apartamento", "Chalet"};
        String[] localidades = {"Alhendin", "Armilla", "Otura"};
        String[] precios = {"60000.50", "55000.00", "100000.30"};
        for (int i = 0; i < direcciones.length; i++) {
            Inmueble inmueble = new Inmueble(direcciones[i], tipos[i], localidades[i], precios[i]);
            in.insert(inmueble);
        }
        Cursor c = in.query();
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
                Intent i = new Intent(this, Anadir.class);
                startActivityForResult(i, CREAR);
                return true;
            case R.id.action_upload:
                subir();
                return true;
            case R.id.action_changeUser:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Cambiar usuario");
                LayoutInflater inflater = LayoutInflater.from(this);
                final View v = inflater.inflate(R.layout.cambiar_usuario, null);
                alert.setView(v);
                alert.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                EditText etUsuario = (EditText) v.findViewById(R.id.etUsuario);
                                String nuevoUsuario = etUsuario.getText().toString();
                                if(nuevoUsuario.length()>0) {
                                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("usuario", nuevoUsuario);
                                    editor.commit();
                                }
                            }
                        });
                alert.setNegativeButton(android.R.string.cancel,null);
                alert.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public ArrayList<Inmueble> buscarNoSubidos() {
        ArrayList<Inmueble> inmuebles = (ArrayList<Inmueble>) in.select(null, null, null);
        ArrayList<Inmueble> inmueblesNoSubidos = new ArrayList<Inmueble>();
        for (Inmueble i : inmuebles) {
            if (i.isSubido() == 0) {
                inmueblesNoSubidos.add(i);
            }
        }
        return inmueblesNoSubidos;
    }

    public void subir() {
        ArrayList<Inmueble> inmueblesNoSubidos = buscarNoSubidos();
        Subir sub = new Subir();
        sub.execute(inmueblesNoSubidos);
    }

    private class Subir extends AsyncTask<ArrayList<Inmueble>, Void, String> {

        String urlCasa = "http://192.168.1.134:8080/InmobiliariaWeb/";
        //String urlClase = "http://192.168.1.130:8080/AAD_Practica3/";
        String controlador = "control?target=inmueble&op=insert&action=movil";
        String controladorFoto = "subida?target=foto&op=insert&action=op";

        public String post(String web, String parametros){
            String todo="";
            try {
                /* Hago la conexión post */
                URL url = new URL(web);
                URLConnection conexion = url.openConnection();
                conexion.setDoOutput(true);
                /* Escribo los parámetros*/
                OutputStreamWriter out = new OutputStreamWriter(conexion.getOutputStream());
                out.write(parametros);
                out.close();
                /* Leo la respuesta */
                BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String linea;
                while ((linea = in.readLine()) != null) {
                    todo += linea + "\n";
                }
                in.close();
            }catch (Exception e){

            }
            return todo;
        }

        private String postFile(String urlPeticion, String nombreParametro, String nombreArchivo, int id) {
            String resultado="";
            int status=0;
            try {
                URL url = new URL(urlPeticion);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.setDoOutput(true);
                conexion.setRequestMethod("POST");

                FileBody fileBody = new FileBody(new File(nombreArchivo));
                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
                multipartEntity.addPart(nombreParametro, fileBody);
                multipartEntity.addPart("id", new StringBody(""+id));
                conexion.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
                OutputStream out = conexion.getOutputStream();
                try {
                    multipartEntity.writeTo(out);
                } catch(Exception ex){
                    return ex.toString();
                } finally {
                    out.close();
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String decodedString;
                while ((decodedString = in.readLine()) != null) {
                    resultado+=decodedString+"\n";
                }
                in.close();
                status = conexion.getResponseCode();
            } catch (MalformedURLException e) {
                return e.toString();
            } catch (IOException e) {
                return e.toString();
            }
            return resultado+"\n"+status;
        }

        @Override
        protected String doInBackground(ArrayList<Inmueble>... s) {
            String id = "";
            for(Inmueble i : s[0]){
                String direccion = i.getDireccion();
                String tipo = i.getTipo();
                String localidad = i.getLocalidad();
                String precio = i.getPrecio()+"";
                String usuario = String.valueOf(getSharedPreferences("usuario", Context.MODE_PRIVATE));
                id = post(urlCasa+controlador,
                        "direccion="+direccion+"&localidad="+localidad+"&tipo="+tipo+"&usuario="+usuario+"&precio="+precio+"");
                id = id.trim();
                Log.v("AAAAAAAAAAAAA","ID: "+id);
                i.setSubido(1);
                in.update(i);
                int idNum = Integer.parseInt(id);
                ArrayList<String> fotos = getFotos(i.getId());
                Log.v("aaaaaaaaaaaa",fotos.size()+"asd");
                for(String foto: fotos) {
                    postFile(urlCasa+controladorFoto, "foto", foto, idNum);
                }
            }
//            r = post("http://192.168.208.120:8080/InmobiliariaWeb/control?target=inmueble&op=insert&action=op",
//                    "direccion=aaa&localidad=aaa&tipo=aaaa&usuario=aaaa&precio=aaa");
            return id;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.v("AAAAAAAAAAAAA", s);
        }
    }

    private ArrayList<String> getFotos(long i){
        ArrayList<String> fotosSubir = new ArrayList<String>();
        String ruta = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath();
        File dir = new File(ruta);
        File[] fotos1 = dir.listFiles();
        for (File foto : fotos1) {
            String idIn = "";
            idIn = foto.getName().split("_")[1];
            long id2 = Long.parseLong(idIn);
            if (id2 == i) {
                fotosSubir.add(foto.getPath());
            }
        }
        return fotosSubir;
    }



    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.long_clic, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int index = info.position;
        List<Inmueble> al = in.select(null, null, null);
        Inmueble i = al.get(index);
        if (id == R.id.action_borrar) {
            in.delete(i);
            actualizarLista();
        } else if (id == R.id.action_modificar) {
            Intent intent = new Intent(this, Anadir.class);
            Bundle b = new Bundle();
            b.putSerializable("inmueble", i);
            b.putInt("index", index);
            intent.putExtras(b);
            startActivityForResult(intent, MODIFICAR);
        } else if (id == R.id.action_foto) {
            inmuebleFoto = i;
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, FOTO);
        }
        return super.onContextItemSelected(item);
    }

    public void actualizarLista() {
        Cursor c = in.query();
        ad.changeCursor(c);
    }


}