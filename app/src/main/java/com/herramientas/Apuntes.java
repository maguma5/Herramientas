package com.herramientas;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Apuntes extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    TextView fecha, operario, herramienta, estado;
    EditText hora;
    Vector<String> listaoperarios = new Vector<>();
    Vector<String> listaherramientas = new Vector<>();
    Vector<String> listaestados = new Vector<>();
    int eleccionHerramienta, eleccionOperario, eleccionEstado;
    Button entrada;

    Vector<Vector<String>> fichero = new Vector<>();
    String[] vector = new String[5];

    String[] archivos;
    String texto;
    String entradapunte;

    String ets1, ets2, ets3, ets4, ets5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apunte);

        entrada = findViewById(R.id.buttonApunte);
        hora = findViewById(R.id.editTextDate);
        fecha = findViewById(R.id.textFecha);
        operario = findViewById(R.id.textOperario);
        herramienta = findViewById(R.id.textHerramienta);
        estado = findViewById(R.id.textEstado);

        SpannableString content = new SpannableString("HORA/FECHA:");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        fecha.setText(content);
        content = new SpannableString("OPERARIO:");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        operario.setText(content);
        content = new SpannableString("HERRAMIENTA:");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        herramienta.setText(content);
        content = new SpannableString("ESTADO:");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        estado.setText(content);

         db = FirebaseFirestore.getInstance();

        hora.setFocusable(false);

        hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calgar_fecha();
            }
        });

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("Usuario", "Luis");
        user.put("Hora", "20 de noviembre de 2024, 8:02:1 p.m. UTC+1" +
                "(marca de tiemp");
        user.put("Maquina", "Radial");
        user.put("Estado","Activo");

// Add a new document with a generated ID
        db.collection("Furrier")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Apuntes.this, "documento añadido: " + documentReference.getId(), Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        // Read data from the database
        db.collection("Furrier")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


        calgar_fecha();
        calgar_archivo();
        listaOperarios();
        listaEstados();
        listaHerramientas();

    }

    //metodo para leer el archivo
    private void calgar_archivo() {
        File externalDir = Environment.getExternalStorageDirectory();
        File textFile = new File(externalDir.getAbsolutePath()+ File.separator + "datosFurrier.txt");
        archivos = externalDir.list();
        if (existe(archivos,"datosFurrier.txt")){

            try {
                texto = " "+readTextFile(textFile);

                int i = 0;
                int posicion1 = 0;
                int posicionf = texto.indexOf(";", posicion1);

                while (posicionf >= 0) {

                    int posicion2 = texto.indexOf(",", posicion1);
                    int posicion3 = texto.indexOf(",", posicion2 + 1);
                    int posicion4 = texto.indexOf(",", posicion3 + 1);
                    int posicion5 = texto.indexOf(",", posicion4 + 1);

                    vector[0] = (texto.substring(posicion1 + 1, posicion2)); // herramienta
                    vector[1] = (texto.substring(posicion2 + 1, posicion3)); // fecha
                    vector[2] = (texto.substring(posicion3 + 1, posicion4)); // operario
                    vector[3] = (texto.substring(posicion4 + 1, posicion5)); // estado
                    vector[4] = (texto.substring(posicion5 + 1, posicionf)); // tipo
                    posicion1 = posicionf + 1;
                    posicionf = texto.indexOf(";", posicion1);
                    List<String> list = Arrays.asList(vector);
                    Vector<String> mivector = new Vector<>(list);
                    fichero.add(mivector);
                    i = i + 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {try {
            inici_archivo();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }}


    }

    private void inici_archivo() throws IOException {
        File externalDir = Environment.getExternalStorageDirectory();
        File textFile = new File(externalDir.getAbsolutePath()
                + File.separator + "datosFurrier.txt");

        String texto1 = "";
        writeTextFile(textFile, texto1);
        calgar_archivo();
    }

    private void writeTextFile(File textFile, String texto) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(textFile));
        writer.write(texto);
        writer.close();
    }

    private String readTextFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder texto2 = new StringBuilder();
        String line;
        while ((line = reader.readLine())!=null){
            texto2.append(line);
            texto2.append("\n");
        }
        reader.close();
        return texto2.toString();
    }

    private boolean existe(String[] archivos, String s) {
        // TODO Auto-generated method stub
        boolean bo = true;
        for (int f = 0; f < archivos.length; f++)
            if (s.equals(archivos[f])) {
                bo = true;
                break;
            } else bo = false;
        return bo;
    }

    private void calgar_fecha() {
        Date today = new Date();
        String fecha_actual = "";
        int hora_actual = today.getHours();
        int minuto_actual = today.getMinutes();
        int dia_actual = today.getDate();
        int mes_actual = today.getMonth() + 1;
        int año_actual = today.getYear() + 1900;
        if (minuto_actual < 10) {
            fecha_actual = hora_actual + ":0" + minuto_actual + " / " + dia_actual + "." + mes_actual + "." + año_actual;
        } else {
            fecha_actual = hora_actual + ":" + minuto_actual + " / " + dia_actual + "." + mes_actual + "." + año_actual;
        }
        hora.setText(fecha_actual);
    }

    private void listaOperarios() {
        if (listaoperarios.size() == 0) {
            listaoperarios=calgar_archivo_operario();
            listaoperarios.add("NUEVO OPERARIO");
        }
        //
        Spinner spinner01 = (Spinner) findViewById(R.id.spinnerOperario);

        ArrayAdapter<String> spinner02 = new ArrayAdapter<String>(
                this, R.layout.custom_spinner_item, listaoperarios);
        spinner02.setDropDownViewResource(R.layout.custom_spinner_item);

        spinner01.setAdapter(spinner02);
        spinner01.setSelection(eleccionOperario);
        spinner01.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adaptador, View v,
                                       int position, long id) {
                eleccionOperario = position;
                if (listaoperarios.get(position).equals("NUEVO OPERARIO")){
                    showDateRangeDialog(1);
                }
                //Toast.makeText(Apuntes.this,"su eleccion es  "+listaoperarios.get(position),Toast.LENGTH_LONG).show();
            }
            public void onNothingSelected(AdapterView<?> adaptador) {
                // your code here
            }
        });
    }
    private Vector<String> calgar_archivo_operario() {
        calgar_archivo();
        for (int i4 = 0; i4 < fichero.size(); i4=i4+1){
            listaoperarios.add(((Vector<String>)fichero.get(i4)).get(2));
        }

        HashSet<String> hs = new HashSet<String>();
        hs.addAll(listaoperarios);
        listaoperarios.clear();
        listaoperarios.addAll(hs);

        return listaoperarios;
    }
    private void listaEstados() {
        if (listaestados.size() == 0) {
            listaestados=calgar_archivo_estados();
            listaestados.add("NUEVO ESTADO");
        }
        //Toast.makeText(Apuntes.this,"ESTAMOS  "+listaspinner,Toast.LENGTH_LONG).show();
        Spinner spinner01 = (Spinner) findViewById(R.id.spinnerEstado);

        ArrayAdapter<String> spinner02 = new ArrayAdapter<String>(
                this, R.layout.custom_spinner_item, listaestados);
        spinner02.setDropDownViewResource(R.layout.custom_spinner_item);

        spinner01.setAdapter(spinner02);
        spinner01.setSelection(eleccionEstado);
        spinner01.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adaptador, View v,
                                       int position, long id) {
                eleccionEstado = position;
                if (listaestados.get(position).equals("NUEVO ESTADO")){
                    showDateRangeDialog(3);
                }
            }
            public void onNothingSelected(AdapterView<?> adaptador) {
                // your code here
            }
        });
    }
    private Vector<String> calgar_archivo_estados() {
        calgar_archivo();
        for (int i4 = 0; i4 < fichero.size(); i4=i4+1){
            listaestados.add(((Vector<String>)fichero.get(i4)).get(3));
        }

        HashSet<String> hs = new HashSet<String>();
        hs.addAll(listaestados);
        listaestados.clear();
        listaestados.addAll(hs);

        return listaestados;
    }
    private void listaHerramientas() {

        if (listaherramientas.size() == 0) {
            listaherramientas=calgar_archivo_herramientas();
            listaherramientas.add("NUEVA HERRAMIENTA");
        }
        //Toast.makeText(Apuntes.this,"ESTAMOS  "+listaspinner,Toast.LENGTH_LONG).show();
        Spinner spinner01 = (Spinner) findViewById(R.id.spinnerHerramienta);

        ArrayAdapter<String> spinner02 = new ArrayAdapter<String>(
                this, R.layout.custom_spinner_item, listaherramientas);
        spinner02.setDropDownViewResource(R.layout.custom_spinner_item);

        spinner01.setAdapter(spinner02);
        spinner01.setSelection(eleccionHerramienta);
        spinner01.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adaptador, View v,
                                       int position, long id) {
                eleccionHerramienta = position;
                if (listaherramientas.get(position).equals("NUEVA HERRAMIENTA")){
                    showDateRangeDialog(2);
                }
            }
            public void onNothingSelected(AdapterView<?> adaptador) {
                // your code here
            }
        });

    }
    private Vector<String> calgar_archivo_herramientas() {
        calgar_archivo();

         for (int i4 = 0; i4 < fichero.size(); i4=i4+1){
            listaherramientas.add(((Vector<String>)fichero.get(i4)).get(0));
        }

        HashSet<String> hs = new HashSet<String>();
        hs.addAll(listaherramientas);
        listaherramientas.clear();
        listaherramientas.addAll(hs);

        return listaherramientas;
    }

    public void addapunte(View view) throws IOException {
        // TODO Auto-generated method stub

        File externalDir = Environment.getExternalStorageDirectory();
        File textFile = new File(externalDir.getAbsolutePath()+ File.separator + "datosFurrier.txt");

        ets1=listaherramientas.get(eleccionHerramienta);// herramienta
        ets2=hora.getText().toString();// fecha
        ets3=listaoperarios.get(eleccionOperario).toString();// concepto
        ets4=listaestados.get(eleccionEstado).toString();// cantidad
        ets5="1";// clave asiento de trabajo

        entradapunte = ets1+","+ets2+","+ets3+","+ets4+","+ets5+";"+"\n";

        String texto = readTextFile(textFile);
        String texto1 = texto+entradapunte;
        writeTextFile(textFile, texto1);

        Toast.makeText(Apuntes.this, "APUNTE AÑADIDO", Toast.LENGTH_SHORT).show();

    }

    private void showDateRangeDialog(int clave) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.adddato, null);

        String titulo = null;
        if (clave == 1) {
            titulo = "AÑADIR NUEVO OPERARIO";
        }
        if (clave == 2) {
            titulo = "AÑADIR NUEVA HERRAMIENTA";
        }
        if (clave == 3) {
            titulo = "AÑADIR NUEVO ESTADO";
        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        final EditText Datonuevo = dialogView.findViewById(R.id.datoNuevo);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle(titulo)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String Dato = Datonuevo.getText().toString();
                        if (clave == 1) {
                            listaoperarios.add(Dato);
                        }
                        if (clave == 2) {
                            listaherramientas.add(Dato);
                        }
                        if (clave == 3) {
                            listaestados.add(Dato);
                        }


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }



}
