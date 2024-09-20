package com.scrapper.ai.services;

import com.scrapper.ai.functions.TavilyServiceFunction;
import com.scrapper.ai.model.LLMResponse;
import com.scrapper.ai.model.TavilyRequest;
import com.scrapper.ai.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class LinkedinService {
    @Value("${tavily.key}")
    private String tavilyApiKey;

    private final OpenAiChatClient openAiChatClient;

    public LLMResponse searchLinkedinProfile(String companyName) {
        var promptOptions = OpenAiChatOptions.builder()
                .withFunctionCallbacks(List.of(FunctionCallbackWrapper.builder(new TavilyServiceFunction(tavilyApiKey))
                        .withName("LinkedinProfileSearchAssistant")
                        .withDescription("Search the current linkedin profile by the company name")
                        .withResponseConverter(response -> {
                            String schema = ModelOptionsUtils.getJsonSchema(TavilyRequest.class, false);
                            String json = ModelOptionsUtils.toJsonString(response);
                            return schema + "\n" + json;
                        })
                        .build()))
                .build();

        Message userMessage = new PromptTemplate(companyName).createMessage();

        Message systemMessage = new SystemPromptTemplate("You are a LinkedIn profile search assistant. " +
                "Your task is to find the most relevant LinkedIn account based on information provided from a Tavily Service search, which includes the company name. " +
                "Analyze the URL links from the search results to identify the one that best matches the company name and context provided. " +
                "Once found, format the LinkedIn URL as https://www.linkedin.com/company/COMPANY_NAME_HERE/people/, COMPANY_NAME_HERE use from the link. " +
                "Return answer in JSON format with one field 'link'. Value example of this field 'link:https://www.linkedin.com/company/the-mobility-house/people/'").createMessage();

        var response = openAiChatClient.call(new Prompt(List.of(userMessage, systemMessage), promptOptions));

        log.info("LLM response: {}", response.getResult().getOutput().getContent());
        return JsonUtils.convert(response.getResult().getOutput().getContent(), LLMResponse.class);
    }

}
