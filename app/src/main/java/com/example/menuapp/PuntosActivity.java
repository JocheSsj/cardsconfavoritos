package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PuntosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntos);

        // Vinculamos el botón con su ID en el layout
        Button button = findViewById(R.id.mapButton);

        // Configuramos el evento click del botón
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia la actividad MapsActivity
                Intent intent = new Intent(PuntosActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
