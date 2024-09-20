package com.scrapper.ai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TavilyResult {
    @JsonProperty("title")
    private String title;
    @JsonProperty("url")
    private String url;
    @JsonProperty("content")
    private String content;
    @JsonProperty("raw_content")
    private String rawContent;
    @JsonProperty("score")
    private Float score;
    @JsonProperty("published_date")
    private String publishedDate;
}
