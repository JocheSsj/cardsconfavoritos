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

public class EstablecimientoAdapter extends RecyclerView.Adapter<EstablecimientoAdapter.ViewHolder> {

    private List<Establecimiento> establecimientos;

    public EstablecimientoAdapter(List<Establecimiento> establecimientos) {
        this.establecimientos = establecimientos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_establecimiento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Establecimiento establecimiento = establecimientos.get(position);

        // Configurar textos
        holder.tvNombre.setText(establecimiento.getNombre());
        holder.tvDireccion.setText(establecimiento.getDireccion());
        holder.tvTipo.setText(establecimiento.getTipo());
        holder.tvDescripcion.setText(establecimiento.getDescripcion());

        // Configurar el estado inicial del botón favorito
        if (establecimiento.getFavorito() == 1) {
            holder.btnFavorito.setImageResource(R.drawable.baseline_star_rate_24); // Estrella llena
        } else {
            holder.btnFavorito.setImageResource(R.drawable.vaciastar_24); // Estrella vacía
        }

        // Manejar clic en el botón favorito
        holder.btnFavorito.setOnClickListener(v -> {
            boolean esFavorito = establecimiento.getFavorito() == 1;

            // Cambiar el estado de favorito en el objeto
            establecimiento.setFavorito(esFavorito ? 0 : 1);

            // Actualizar el icono
            holder.btnFavorito.setImageResource(
                    esFavorito ? R.drawable.vaciastar_24 : R.drawable.baseline_star_rate_24
            );

            // Guardar el cambio en la base de datos
            DbHelper dbHelper = new DbHelper(holder.itemView.getContext());
            dbHelper.actualizarFavorito(establecimiento.getId(), establecimiento.getFavorito());
        });
    }

    @Override
    public int getItemCount() {
        return establecimientos.size();
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
            btnFavorito = itemView.findViewById(R.id.btnFavorito);
        }
    }
}
