package com.biblioteca.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

