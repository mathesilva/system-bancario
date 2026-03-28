package com.example.systembank.entity;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
public class Transacao implements Persistable<UUID> {
    @Id
    private UUID id;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(name = "data_transacao")
    private LocalDateTime dataTransacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacao status;

    @Transient
    private boolean isNew = true;

    public Transacao() {}

    public Transacao(UUID id, BigDecimal saldo, LocalDateTime dataTransacao, StatusTransacao status) {
        this.id = id;
        this.saldo = saldo;
        this.dataTransacao = dataTransacao;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public StatusTransacao getStatus() {
        return status;
    }

    public void setStatus(StatusTransacao status) {
        this.status = status;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
