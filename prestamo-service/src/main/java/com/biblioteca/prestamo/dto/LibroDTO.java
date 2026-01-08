package com.biblioteca.prestamo.dto;

public class LibroDTO {
    private Long id;
    private String titulo;
    private String autor;
    private Integer stock;

    public LibroDTO() {
    }

    public LibroDTO(Long id, String titulo, String autor, Integer stock) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}

