package com.scrapper.ai.functions;

import com.scrapper.ai.model.TavilyRequest;
import com.scrapper.ai.model.TavilyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

@Slf4j
public class TavilyServiceFunction implements Function<TavilyRequest, TavilyResponse> {
    public static final String TAVILY_URL = "https://api.tavily.com";

    private final String tavilyApiKey;

    public TavilyServiceFunction(String tavilyApiKey) {
        this.tavilyApiKey = tavilyApiKey;
    }

    @Override
    public TavilyResponse apply(TavilyRequest request) {
        request.setApiKey(tavilyApiKey);
        RestClient restClient = RestClient.builder()
                .baseUrl(TAVILY_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Accept", "application/json");
                    httpHeaders.set("Content-Type", "application/json");
                }).build();

        return restClient
                .post()
                .uri(uriBuilder -> {
                    log.info("Building URI for tavily request {}", request);
                    return uriBuilder.path("/search").build();
                })
                .body(request)
                .retrieve()
                .body(TavilyResponse.class);
    }
}
