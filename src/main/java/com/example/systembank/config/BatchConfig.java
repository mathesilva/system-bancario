package com.example.systembank.config;

import com.example.systembank.dto.TransacaoDTO;
import com.example.systembank.entity.Transacao;
import com.example.systembank.processor.TransacaoProcessor;
import com.example.systembank.repository.TransacaoRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;


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

    @Bean
    public RepositoryItemWriter<Transacao> writer (TransacaoRepository repository){
        return new RepositoryItemWriterBuilder<Transacao>()
                .repository(repository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step stepImportacao(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               FlatFileItemReader<TransacaoDTO> reader, TransacaoProcessor processor,
                               RepositoryItemWriter<Transacao> writer){
        return new StepBuilder("stepImportacaoTransacoes", jobRepository)
                .<TransacaoDTO, Transacao>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step stepImportacao){
        return new JobBuilder("jobImportacao", jobRepository)
                .start(stepImportacao)
                .build();
    }
}
