package com.example.rutasegura;

import java.io.Serializable;

public class Reporte implements Serializable {
    private String id;
    private String titulo;
    private String categoria;
    private String descripcion;
    private String fechaHora;
    private String estado;
    private double latitud;
    private double longitud;
    private float valorSensor;

    // Constructor vacío obligatorio para Firestore
    public Reporte() {
    }

    public Reporte(String titulo, String categoria, String descripcion, String fechaHora,
                   String estado, double latitud, double longitud, float valorSensor) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.latitud = latitud;
        this.longitud = longitud;
        this.valorSensor = valorSensor;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public float getValorSensor() { return valorSensor; }
    public void setValorSensor(float valorSensor) { this.valorSensor = valorSensor; }
}