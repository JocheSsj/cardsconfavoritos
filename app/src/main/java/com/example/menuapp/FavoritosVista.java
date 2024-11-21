package com.example.menuapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritosVista extends AppCompatActivity {
    RecyclerView recyclerView;
    FavoritosAdapter adapter; // Cambié de InstitutoAdapter a FavoritosAdapter
    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar diseño a pantalla completa
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_favoritos_vista);

        // Configurar insets para manejar barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFavoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener datos de favoritos desde la base de datos
        dbHelper = new DbHelper(this);
        List<Favoritos> favoritos = dbHelper.obtenerFavoritos();

        // Configurar adaptador
        adapter = new FavoritosAdapter(favoritos); // Usar FavoritosAdapter
        recyclerView.setAdapter(adapter);
    }
}
