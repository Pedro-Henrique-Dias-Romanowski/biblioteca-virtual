package com.pedrohenrique.bibliotecavirtual.domain.entity;


import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Administrador {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private Perfil perfil;
}
