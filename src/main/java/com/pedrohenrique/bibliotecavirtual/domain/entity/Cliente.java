package com.pedrohenrique.bibliotecavirtual.domain.entity;

import com.pedrohenrique.bibliotecavirtual.domain.enums.Perfil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private Integer qtLivrosEmprestados;
    private Perfil perfil;
}
