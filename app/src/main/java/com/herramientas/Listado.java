package com.herramientas;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class Listado extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WorkerAdapter workerAdapter;
    private List<Worker> workerList;
    Vector<Vector<String>> fichero = new Vector<Vector<String>>();
    String[] vector = new String[5];

    private List<String> workers;
    private List<String> tools;

    String[] archivos;
    String texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listado);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));// 1 columna por fila
        workerList = new ArrayList<>();
        calgar_archivo();
        trabajadores();
        maquinas();
        alertlistas();

    }
    //metodo para mostrar el dialogo para elegir entre mostrar operarios o herramientas
    private void alertlistas() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione una opción");
        builder.setView(dialogView);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == R.id.radio_worker) {
                    // Lógica para mostrar operarios
                    showWorkerSelectionDialog();
                } else if (selectedId == R.id.radio_tool) {
                    // Lógica para mostrar herramientas
                   showToolSelectionDialog();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //metodo para mostrar que maquinas a usado un operario
    private void showWorkerSelectionDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_select_worker, null);
        Spinner spinnerWorker = dialogView.findViewById(R.id.spinner_worker);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, workers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorker.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona un operario");
        builder.setView(dialogView);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedWorker = (String) spinnerWorker.getSelectedItem();
                // Lógica para mostrar herramientas usadas por el operario seleccionado
                operarioHerramientas(selectedWorker);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    //metodo para mostrar que usuarios han usado un maquina
    private void showToolSelectionDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_select_tool, null);
        Spinner spinnerTool = dialogView.findViewById(R.id.spinner_tool);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tools);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTool.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una herramienta");
        builder.setView(dialogView);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedTool = (String) spinnerTool.getSelectedItem();
                // Lógica para mostrar operarios que han usado la herramienta seleccionada
                herramientaOperario(selectedTool);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    //metodo para leer el archivo
    private void calgar_archivo() {
        File externalDir = Environment.getExternalStorageDirectory();
        File textFile = new File(externalDir.getAbsolutePath() + File.separator + "datosFurrier.txt");
        archivos = externalDir.list();
        if (existe(archivos, "datosFurrier.txt")) {

            try {
                texto = " " + readTextFile(textFile);

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
                    Vector<String> mivector = new Vector<String>(list);
                    fichero.add(mivector);
                    workerList.add(new Worker(vector[0], vector[1], vector[2], vector[3]));
                    i = i + 1;
                    workerAdapter = new WorkerAdapter(workerList);
                    recyclerView.setAdapter(workerAdapter);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                inici_archivo();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //metodo para crear la lista de operarios
    private void trabajadores() {
        workers = new ArrayList<>();
        for (int i3 = 0; i3 < fichero.size(); i3 = i3 + 1) {
            workers.add(((Vector<String>) fichero.get(i3)).get(2));
        }
        // Eliminar duplicados usando un conjunto
        workers = new ArrayList<>(new HashSet<>(workers));
    }

    //metodo para crear la lista de herramientas
    private void maquinas() {
        tools = new ArrayList<>();
        for (int i3 = 0; i3 < fichero.size(); i3 = i3 + 1) {
            tools.add(((Vector<String>) fichero.get(i3)).get(0));
        }
        // Eliminar duplicados usando un conjunto
        tools = new ArrayList<>(new HashSet<>(tools));
    }

    //metodo para crear las herramientas retiradas por un operario
    private void operarioHerramientas(String op) {
        workerList.clear();
        for (int i3 = 0; i3 < fichero.size(); i3 = i3 + 1) {
            if (((Vector<String>) fichero.get(i3)).get(2).equals(op)) {
                workerList.add(new Worker(fichero.get(i3).get(0), fichero.get(i3).get(1), null, fichero.get(i3).get(3)));
            }
        }
        workerAdapter = new WorkerAdapter(workerList);
        recyclerView.setAdapter(workerAdapter);
    }
    //metodo para crear los operarios que han usado una herramienta
    private void herramientaOperario(String he) {
        workerList.clear();
        for (int i3 = 0; i3 < fichero.size(); i3 = i3 + 1) {
            if (((Vector<String>) fichero.get(i3)).get(0).equals(he)) {
                workerList.add(new Worker(null, fichero.get(i3).get(1), fichero.get(i3).get(2), fichero.get(i3).get(3)));
            }
        }
        workerAdapter = new WorkerAdapter(workerList);
        recyclerView.setAdapter(workerAdapter);
    }
    //metodo para crear el archivo
    private void inici_archivo() throws IOException {
        File externalDir = Environment.getExternalStorageDirectory();
        File textFile = new File(externalDir.getAbsolutePath()
                + File.separator + "datosFurrier.txt");

        String texto1 = "";
        writeTextFile(textFile, texto1);
        calgar_archivo();
    }
    //metodo para leer el archivo
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
    //metodo para escribir en el archivo
    private void writeTextFile(File textFile, String texto) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(textFile));
        writer.write(texto);
        writer.close();
    }
    //metodo para saber si existe el archivo
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

}