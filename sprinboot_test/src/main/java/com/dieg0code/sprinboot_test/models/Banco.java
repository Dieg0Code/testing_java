package com.dieg0code.sprinboot_test.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "bancos")
public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private String nombre;

    @Column(name = "total_transferencias")
    private int totalTransferencias;

    public Banco() {
    }

    public Banco(Long id, String nombre, int totalTransferencias) {
        this.id = id;
        this.nombre = nombre;
        this.totalTransferencias = totalTransferencias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTotalTransferencias() {
        return totalTransferencias;
    }

    public void setTotalTransferencias(int totalTransferencias) {
        this.totalTransferencias = totalTransferencias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Banco banco = (Banco) o;
        return totalTransferencias == banco.totalTransferencias && Objects.equals(id, banco.id) && Objects.equals(nombre, banco.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, totalTransferencias);
    }
}
