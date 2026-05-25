package com.biblioteca.auth.dto;

import com.biblioteca.auth.service.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDto {
    private Long id;
    private String usuario;
    private String password;
    private Rol rol;


}
