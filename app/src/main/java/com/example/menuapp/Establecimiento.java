package com.example.menuapp;
public class Establecimiento {
    private String id;
    private String nombre;
    private String direccion;
    private String tipo;
    private String descripcion;
    private int favorito;

    public Establecimiento() {} // Constructor vacío requerido por Firebase

    public Establecimiento(String nombre, String direccion, String tipo, String descripcion, int favorito) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.favorito = favorito;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getFavorito() {
        return favorito;
    }

    public void setFavorito(int favorito) {
        this.favorito = favorito;
    }
}
