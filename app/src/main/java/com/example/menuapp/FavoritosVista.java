package com.example.menuapp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritosVista extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoritosAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos_vista);

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFavoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar el adaptador
        adapter = new FavoritosAdapter(this);
        recyclerView.setAdapter(adapter);
    }
}
