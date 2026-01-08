package com.biblioteca.prestamo.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class PrestamoEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long prestamoId;
    private Long libroId;
    private String username;
    private LocalDate fechaPrestamo;
    private EventType eventType;

    public enum EventType {
        PRESTAMO_CREADO,
        PRESTAMO_DEVUELTO
    }

    public PrestamoEvent() {
    }

    public PrestamoEvent(Long prestamoId, Long libroId, String username, LocalDate fechaPrestamo, EventType eventType) {
        this.prestamoId = prestamoId;
        this.libroId = libroId;
        this.username = username;
        this.fechaPrestamo = fechaPrestamo;
        this.eventType = eventType;
    }

    public Long getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(Long prestamoId) {
        this.prestamoId = prestamoId;
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "PrestamoEvent{" +
                "prestamoId=" + prestamoId +
                ", libroId=" + libroId +
                ", username='" + username + '\'' +
                ", fechaPrestamo=" + fechaPrestamo +
                ", eventType=" + eventType +
                '}';
    }
}

