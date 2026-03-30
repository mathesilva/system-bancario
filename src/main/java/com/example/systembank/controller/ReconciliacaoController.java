package com.example.systembank.controller;

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
public class ReconciliacaoController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

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