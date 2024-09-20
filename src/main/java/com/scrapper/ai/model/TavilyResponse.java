package com.scrapper.ai.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@JsonClassDescription("Tavily API Response. 'results' is main response field")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TavilyResponse {
    @JsonProperty("answer")
    private String answer;
    @JsonProperty("query")
    private String query;
    @JsonProperty("response_time")
    private Float responseTime;
    @JsonProperty("images")
    private List<String> images;
    @JsonPropertyDescription("'results' field for analyzing response and searching linkedin profile url'")
    @JsonProperty("results")
    private List<TavilyResult> results;
}
