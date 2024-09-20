package com.scrapper.ai.controllers;

import com.scrapper.ai.services.CSVAnalyzer;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@RestController
public class CSVController {
    private final CSVAnalyzer csvAnalyzer;

    @Operation(summary = "Ready to use")
    @PostMapping(
            value = "/uploadFile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ByteArrayResource> uploadFileHandler(@RequestParam("file") MultipartFile file) {
        String result = csvAnalyzer.analyzeCsv(file);

        byte[] content = result.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(content);

        return ResponseEntity.ok()
                .contentLength(content.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "result.csv" + "\"")
                .body(resource);
    }
}
