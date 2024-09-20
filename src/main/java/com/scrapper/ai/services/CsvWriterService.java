package com.scrapper.ai.services;

import com.scrapper.ai.model.LinkedinProfile;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;

import static com.opencsv.ICSVWriter.*;

@Slf4j
@Service
public class CsvWriterService {
    private static final String[] HEADERS = {
            "Name",
            "EvTrader",
            "Linkedin"
    };

    public String writeObjectsToCsv(List<LinkedinProfile> profiles) {

        try (StringWriter sw = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(sw, ',', NO_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END)
        ) {
            csvWriter.writeNext(HEADERS);
            profiles.forEach(transaction -> csvWriter.writeNext(transactionToCsvData(transaction)));
            return sw.toString();
        } catch (Exception e) {
            log.error("Exception during creation CSV file with profiles", e);
            return "";
        }
    }

    private String[] transactionToCsvData(LinkedinProfile profile) {
        return new String[] {
                profile.getCompanyName(),
                profile.getEvTraderUrl(),
                profile.getProfileUrl()
        };
    }
}
