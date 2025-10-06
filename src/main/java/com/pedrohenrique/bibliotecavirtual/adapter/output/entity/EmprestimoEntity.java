package com.pedrohenrique.bibliotecavirtual.adapter.output.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "emprestimo")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class EmprestimoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteEntity clienteId;

    @ManyToMany
    @JoinTable(
            name = "livro_empresimo",
            joinColumns = @JoinColumn(name = "emprestimo_id"),
            inverseJoinColumns = @JoinColumn(name = "livro_id")
    )
    private List<LivroEntity> livros;

    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;
    private Boolean devolvido;
}
