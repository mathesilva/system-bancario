package com.example.systembank.processor;

import com.example.systembank.dto.TransacaoDTO;
import com.example.systembank.entity.StatusTransacao;
import com.example.systembank.entity.Transacao;
import com.example.systembank.repository.TransacaoRepository;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Component
public class TransacaoProcessor implements ItemProcessor<TransacaoDTO, Transacao> {

    @Autowired
    private final TransacaoRepository transacaoRepository;

    public TransacaoProcessor(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    @Override
    public Transacao process(TransacaoDTO dto) throws Exception{
        UUID idCsv = UUID.fromString(dto.getId());
        BigDecimal valorCsv = new BigDecimal(dto.getValor());
        LocalDateTime dataCsv = LocalDateTime.parse(dto.getDataTransacao(), DateTimeFormatter.ISO_DATE_TIME);

        Optional<Transacao> transacaoBanco = transacaoRepository.findById(idCsv);

        if (transacaoBanco.isEmpty()){
            System.out.println("LOG: Transação " + idCsv + "não encontrada no banco. Marcando como DIVERGENTE.");
            return criarTransacaoDivergente(idCsv, valorCsv, dataCsv);
        }

        Transacao dbRecord = transacaoBanco.get();

        boolean valoresBatem = dbRecord.getSaldo().compareTo(valorCsv) == 0;
        boolean datasBatem = dbRecord.getDataTransacao().isEqual(dataCsv);

        if (valoresBatem && datasBatem){
            dbRecord.setStatus(StatusTransacao.CONCILIADO);
            System.out.println("LOG: Transação " + idCsv + " CONCILIADA com sucesso!");
        } else{
            dbRecord.setStatus(StatusTransacao.DIVERGENTE);
            System.out.println("LOG: Transação " + idCsv + " com valores ou datas diferentes. Marcando como DIVERGENTE.");
        }
        return dbRecord;
        }

        public Transacao criarTransacaoDivergente(UUID id, BigDecimal valor, LocalDateTime dataTransacao){
            Transacao transacao = new Transacao();
            transacao.setId(id);
            transacao.setSaldo(valor);
            transacao.setDataTransacao(dataTransacao);
            transacao.setStatus(StatusTransacao.DIVERGENTE);
            return transacao;
        }
    }
