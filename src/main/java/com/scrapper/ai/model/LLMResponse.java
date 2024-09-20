package com.scrapper.ai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LLMResponse {
    @JsonPropertyDescription("Link with Linkedin Profile")
    @JsonProperty("link")
    private String link;
}
