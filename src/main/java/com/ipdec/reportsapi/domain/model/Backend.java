package com.ipdec.reportsapi.domain.model;

import com.ipdec.reportsapi.api.dto.BackendInputDto;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "backend", schema = "cadastro")
@Data
public class Backend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "url")
    private String url;

    @Column(name = "token")
    private String token;

    @OneToMany(mappedBy = "backend")
    private List<Relatorio> relatorios = new ArrayList<>();

    @Column(name = "criado_em")
    private Date criadoEm;

    @Column(name = "atualizado_em")
    private Date atualizadoEm;

    public Backend() {
    }

    public Backend(BackendInputDto dto) {
        this.nome = dto.getNome();
        this.descricao = dto.getDescricao();
        this.url = dto.getUrl();
        this.token = new BCryptPasswordEncoder().encode(dto.getToken());
        this.criadoEm = new Date();
    }
}
