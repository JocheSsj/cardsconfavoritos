package com.example.menuapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ColegiosAdapter extends RecyclerView.Adapter<ColegiosAdapter.ViewHolder> {

    private List<Colegios> colegios;

    public ColegiosAdapter(List<Colegios> colegios) {
        this.colegios = colegios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_colegios, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Colegios colegio = colegios.get(position);

        // Configurar textos
        holder.tvNombre.setText(colegio.getNombre());
        holder.tvDireccion.setText(colegio.getDireccion());
        holder.tvTipo.setText(colegio.getTipo());
        holder.tvDescripcion.setText(colegio.getDescripcion());

        // Configurar el estado inicial del botón favorito
        if (colegio.getFavorito() == 1) {
            holder.btnFavorito.setImageResource(R.drawable.baseline_star_rate_24); // Estrella llena
        } else {
            holder.btnFavorito.setImageResource(R.drawable.vaciastar_24); // Estrella vacía
        }

        // Manejar clic en el botón favorito
        holder.btnFavorito.setOnClickListener(v -> {
            boolean esFavorito = colegio.getFavorito() == 1;

            // Cambiar el estado de favorito en el objeto
            colegio.setFavorito(esFavorito ? 0 : 1);

            // Actualizar el icono
            holder.btnFavorito.setImageResource(
                    esFavorito ? R.drawable.vaciastar_24 : R.drawable.baseline_star_rate_24
            );

            // Guardar el cambio en la base de datos
            DbHelper dbHelper = new DbHelper(holder.itemView.getContext());
            dbHelper.actualizarFavorito(colegio.getId(), colegio.getFavorito());
        });

        // Manejar clic en el botón "Ver Más"
        holder.itemView.findViewById(R.id.btnmas).setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), Detalles.class);
            intent.putExtra("nombre", colegio.getNombre()); // Enviar nombre al DetallesActivity
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return colegios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDireccion, tvTipo, tvDescripcion;
        ImageButton btnFavorito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            btnFavorito = itemView.findViewById(R.id.btnFavorito); // Botón de favoritos
        }
    }
}
