package com.example.menuapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GeneralVista extends AppCompatActivity {

    RecyclerView recyclerView;
    GeneralAdapter adapter;
    Button btnPublicos, btnPrivados, btnRestablecer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_vista);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewGeneral);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar adaptador
        adapter = new GeneralAdapter(this);
        recyclerView.setAdapter(adapter);

        // Configurar botones
        btnPublicos = findViewById(R.id.btnPublicos);
        btnPrivados = findViewById(R.id.btnPrivados);
        btnRestablecer = findViewById(R.id.btnRestablecer);

        // Botón "Públicos"
        btnPublicos.setOnClickListener(v -> {
            adapter.filtrarPorTipo("Publica"); // Filtrar públicos
        });

        // Botón "Privados"
        btnPrivados.setOnClickListener(v -> {
            adapter.filtrarPorTipo("Privada"); // Filtrar privados
        });

        // Botón "Restablecer"
        btnRestablecer.setOnClickListener(v -> {
            adapter.restablecerFiltro(); // Restablecer filtro y mostrar todos
        });
    }

}
