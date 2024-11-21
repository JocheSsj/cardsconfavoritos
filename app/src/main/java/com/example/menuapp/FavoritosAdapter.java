package com.example.menuapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> {

    private List<Favoritos> favoritos; // Lista de objetos Favoritos

    // Constructor para inicializar la lista de favoritos
    public FavoritosAdapter(List<Favoritos> favoritos) {
        this.favoritos = favoritos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_favoritos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Favoritos favorito = favoritos.get(position); // Cambié el nombre del objeto local a "favorito"

        // Configurar textos
        holder.tvNombre.setText(favorito.getNombre());
        holder.tvDireccion.setText(favorito.getDireccion());
        holder.tvTipo.setText(favorito.getTipo());
        holder.tvDescripcion.setText(favorito.getDescripcion());

        // Configurar el estado inicial del botón favorito
        if (favorito.getFavorito() == 1) {
            holder.btnFavorito.setImageResource(R.drawable.baseline_star_rate_24); // Estrella llena
        } else {
            holder.btnFavorito.setImageResource(R.drawable.vaciastar_24); // Estrella vacía
        }

        // Manejar clic en el botón favorito
        holder.btnFavorito.setOnClickListener(v -> {
            boolean esFavorito = favorito.getFavorito() == 1;

            // Cambiar el estado de favorito en el objeto
            favorito.setFavorito(esFavorito ? 0 : 1);

            // Actualizar el icono
            holder.btnFavorito.setImageResource(
                    esFavorito ? R.drawable.vaciastar_24 : R.drawable.baseline_star_rate_24
            );

            // Guardar el cambio en la base de datos
            DbHelper dbHelper = new DbHelper(holder.itemView.getContext());
            dbHelper.actualizarFavorito(favorito.getId(), favorito.getFavorito());
        });
    }

    @Override
    public int getItemCount() {
        return favoritos.size();
    }

    // ViewHolder para manejar las vistas del CardView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDireccion, tvTipo, tvDescripcion;
        ImageButton btnFavorito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre); // ID del TextView en cardview_favoritos.xml
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            btnFavorito = itemView.findViewById(R.id.btnFavorito); // ID del ImageButton en cardview_favoritos.xml
        }
    }
}