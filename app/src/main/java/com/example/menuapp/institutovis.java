
package com.example.menuapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class institutovis extends AppCompatActivity {

    RecyclerView recyclerView;
    InstitutoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_institutovis);

        // Configurar ajustes de diseño para manejar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewInstitutos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Usar el adaptador que carga datos desde Firebase
        adapter = new InstitutoAdapter(); // Constructor por defecto
        recyclerView.setAdapter(adapter);
    }
}
