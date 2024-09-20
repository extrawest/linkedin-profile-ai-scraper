package com.scrapper.ai.services;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.scrapper.ai.exceptions.InvalidDataException;
import com.scrapper.ai.model.LinkedinProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class CSVAnalyzer {
    @Value("${multi-threads.enable}")
    private boolean multiThreads;
    @Value("${google.enable}")
    private boolean googleSpreadsheets;

    private final LinkedinService linkedinService;
    private final CsvWriterService csvWriterService;
    private final GoogleApiService googleApiService;

    public String analyzeCsv(MultipartFile file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));

            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreQuotations(true)
                    .build();

            try (CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(0)
                    .withCSVParser(parser)
                    .build()) {

                List<String[]> rows = csvReader.readAll();
                rows.removeFirst();
                List<LinkedinProfile> linkedinProfiles;
                if (multiThreads) {
                    linkedinProfiles = convertMultiThreads(rows);
                } else {
                    linkedinProfiles = convertWithDelay(rows);
                }

                if (googleSpreadsheets) {
                    return googleApiService.createNewSpreadsheet(linkedinProfiles);
                } else {
                    return csvWriterService.writeObjectsToCsv(linkedinProfiles);
                }
            }
        } catch (Exception e) {
            throw new InvalidDataException("Invalid CSV file: " + e.getMessage());
        }
    }

    private List<LinkedinProfile> convertWithDelay(List<String[]> rows) {
        List<LinkedinProfile> result = new LinkedList<>();
        try (ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10)) {
            long delay = 1;

            for (String[] row: rows) {
                scheduler.schedule(() -> convert(row), delay, TimeUnit.MINUTES);
                delay = delay + 1;
            }

            delay = delay + 1;

            scheduler.shutdown();
            boolean awaitTermination = scheduler.awaitTermination(delay, TimeUnit.MINUTES);
            log.info("Await termination result {}", awaitTermination);

            return result;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new InvalidDataException("Delay Thread Exception: " + e.getMessage());
        }
    }

    private List<LinkedinProfile> convertMultiThreads(List<String[]> rows) {
        List<LinkedinProfile> result = new LinkedList<>();
        ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();

        try (ExecutorService executor = Executors.newFixedThreadPool(8, virtualThreadFactory)) {
            List<Future<LinkedinProfile>> futures = new ArrayList<>();

            rows.forEach(row -> {
                Callable<LinkedinProfile> callableTask = () -> convert(row);
                futures.add(executor.submit(callableTask));
            });

            futures.forEach(future -> {
                try {
                    result.add(future.get());
                } catch (Exception e) {
                    log.info("Future was failed", e);
                    Thread.currentThread().interrupt();
                }
            });

            executor.shutdown();

            return result;
        } catch (Exception e) {
            throw new InvalidDataException("Multi Threads Exception: " + e.getMessage());
        }
    }

    private LinkedinProfile convert(String[] row) {
        String companyName = row[0];
        String linkedinUrl = linkedinService.searchLinkedinProfile(companyName).getLink();
        return new LinkedinProfile(companyName, linkedinUrl, row[1]);
    }
}
