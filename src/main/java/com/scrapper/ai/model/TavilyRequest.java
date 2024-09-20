package com.scrapper.ai.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@JsonClassDescription("Tavily API Request. Using for search linkedin profile by the company name")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TavilyRequest {
    @JsonPropertyDescription("Query for the request. Name and country of the company and this text ' Company Linkedin '")
    @JsonProperty("query")
    private String query;
    @JsonPropertyDescription("Api key for the request")
    @JsonProperty("api_key")
    private String apiKey;
    @JsonPropertyDescription("Max size of the result. Must be equals to 20")
    @JsonProperty("max_results")
    private Integer maxResults = 20;
}
