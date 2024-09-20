package com.scrapper.ai.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.scrapper.ai.exceptions.InvalidDataException;
import com.scrapper.ai.model.LinkedinProfile;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;

@RequiredArgsConstructor
@Slf4j
@Service
public class GoogleApiService {
    private static final String[] HEADERS = {
            "Name",
            "EvTrader",
            "Linkedin"
    };

    @Value("${google.key}")
    private String keyPath;
    private Sheets sheets;

    public String createNewSpreadsheet(List<LinkedinProfile> profiles) {
        try {
            Spreadsheet spreadSheet = new Spreadsheet().setProperties(new SpreadsheetProperties().setTitle("Linkedin profiles Spreadsheet: " + System.currentTimeMillis()));
            Spreadsheet sheetCreated = sheets.spreadsheets().create(spreadSheet).execute();

            ValueRange body = new ValueRange().setValues(map(profiles));
            UpdateValuesResponse result = sheets.spreadsheets().values().update(sheetCreated.getSpreadsheetId(), "A1", body).setValueInputOption("RAW").execute();

            return result.getSpreadsheetId();
        } catch (Exception e) {
            throw new InvalidDataException(String.format("Invalid spreadsheet: %s", e.getMessage()));
        }
    }

    @PostConstruct
    public void authorize() throws IOException, GeneralSecurityException {
        Credential credential = initCredential();
        sheets = initSheets(credential);
    }

    private List<List<Object>> map(List<LinkedinProfile> profiles) {
        List<List<Object>> result = new ArrayList<>();
        result.add(Arrays.asList(HEADERS));
        profiles.forEach(profile -> result.add(Arrays.asList(profile.getCompanyName(), profile.getEvTraderUrl(), profile.getProfileUrl())));
        return result;
    }

    private Sheets initSheets(Credential credential) throws IOException, GeneralSecurityException {
        return new Sheets
                .Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("Test")
                .build();
    }

    private Credential initCredential() {
        try {
            File initialFile = new File(keyPath);
            InputStream in = new FileInputStream(initialFile);
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));

            List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
                    .Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), clientSecrets, scopes)
                    .setDataStoreFactory(new MemoryDataStoreFactory())
                    .setAccessType("offline").build();
            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        } catch (Exception e) {
            throw new InvalidDataException(String.format("Invalid client secret: %s", e.getMessage()));
        }
    }

}
