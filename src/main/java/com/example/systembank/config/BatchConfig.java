package com.example.systembank.config;

import com.example.systembank.dto.TransacaoDTO;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;


@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemReader<TransacaoDTO> reader() {

        return new FlatFileItemReaderBuilder<TransacaoDTO>()
                .name("TransacaoItemReader")
                .resource(new ClassPathResource("bank-statements.csv"))
                .delimited()
                .names("id", "valor", "dataTransacao")
                .linesToSkip(1)
                .targetType(TransacaoDTO.class)
                .build();
    }
}
