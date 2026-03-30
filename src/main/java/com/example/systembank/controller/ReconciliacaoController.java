package com.example.systembank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reconciliacao")
@Tag(name = "Conciliação Bancária", description = "Endpoints para gerenciamento do fluxo de processamento em lote (Batch)")
public class ReconciliacaoController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Operation(
            summary = "Dispara o Job de Conciliação",
            description = "Inicia a leitura do arquivo CSV, processa as regras de negócio comparando com o banco de dados e salva o status das transações (Conciliado/Divergente/Novo)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job executado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar o arquivo")
    })
    @PostMapping("/start")
    public ResponseEntity<String> startReconciliacao() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("Tempo executado", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);
            return ResponseEntity.ok("Job de conciliacao disparado com sucesso");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Erro ao disparar o job " + ex.getMessage());
        }
    }
}