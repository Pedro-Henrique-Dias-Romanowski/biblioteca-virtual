package com.pedrohenrique.bibliotecavirtual.adapter.output.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "livro")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class LivroEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String autor;
    private Integer anoPublicacao;
    private Boolean disponivel;

    @ManyToMany(mappedBy = "livros")
    private List<EmprestimoEntity> emprestimos;
}
