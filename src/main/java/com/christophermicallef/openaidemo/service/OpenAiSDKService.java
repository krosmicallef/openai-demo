package com.christophermicallef.openaidemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.core.JsonValue;
import com.openai.models.FunctionDefinition;
import com.openai.models.FunctionParameters;
import com.openai.models.chat.completions.*;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiSDKService implements IAiService {

    private final OpenAIClient openAIClient;
    private final String model;
    private final WeatherService weatherService;

    public OpenAiSDKService(OpenAIClient openAIClient, @Value("${openai.model}") String model, WeatherService weatherService) {
        this.openAIClient = openAIClient;
        this.model = model;
        this.weatherService = weatherService;
    }

    @Override
    public Mono<String> ask(String question) {
        ChatCompletionCreateParams params =
                ChatCompletionCreateParams.builder()
                        .model(model)
                        .addUserMessage(question)
                        .build();
        ChatCompletion completion = openAIClient.chat().completions().create(params);
        String content = completion.choices().getFirst().message().content().orElseThrow();
        return Mono.just(content);
    }

    public Mono<String> askForWeather(String question) throws JsonProcessingException {

        // Define the getWeather tool
        FunctionDefinition getWeatherFunction = getFunctionDefinition();
        ChatCompletionFunctionTool weatherTool = ChatCompletionFunctionTool.builder()
                .function(getWeatherFunction)
                .build();

        // Add question and tool to the ChatCompletion Parameters
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(model)
                .addUserMessage(question)
                .addTool(weatherTool)
                .build();

        // Get Response
        ChatCompletion response = openAIClient.chat().completions().create(params);
        ChatCompletionMessage assistant = response.choices().getFirst().message();

        // No tools selected so just return the content
        if (assistant.toolCalls().isEmpty()) {
            return Mono.just(assistant.content().orElse(""));
        }

        // Get tool selected (we only have 1)
        ChatCompletionMessageToolCall toolCall = assistant.toolCalls().get().getFirst();
        var chatCompletionMessageFunctionToolCall = toolCall.function().orElseThrow();
        String argumentsJson = chatCompletionMessageFunctionToolCall.function().arguments();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> args =
                objectMapper.readValue(argumentsJson, Map.class);
        String city = args.get("city");
        int temperatureForGivenCity = weatherService.getTemperature(city);

        ChatCompletionUserMessageParam userMessage =
                ChatCompletionUserMessageParam.builder()
                        .content(question)
                        .build();

        ChatCompletionToolMessageParam toolMessage =
                ChatCompletionToolMessageParam.builder()
                        .toolCallId(chatCompletionMessageFunctionToolCall.id())
                        .content(String.valueOf(temperatureForGivenCity))
                        .build();

        ChatCompletionAssistantMessageParam assistantParam =
                ChatCompletionAssistantMessageParam.builder()
                        .toolCalls(assistant.toolCalls().get())
                        .build();

        List<ChatCompletionMessageParam> messages = List.of(
                ChatCompletionMessageParam.ofUser(userMessage),
                ChatCompletionMessageParam.ofAssistant(assistantParam),
                ChatCompletionMessageParam.ofTool(toolMessage)
        );

        // Follow-up call
        ChatCompletionCreateParams followUp = ChatCompletionCreateParams.builder()
                .model(model)
                .messages(messages)
                .build();

        // Get final response
        ChatCompletion finalResponse =
                openAIClient.chat().completions().create(followUp);
        String content = finalResponse.choices().getFirst().message().content().orElseThrow();
        return Mono.just(content);
    }

    private static @NonNull FunctionDefinition getFunctionDefinition() {
        return FunctionDefinition.builder()
                .name("getWeather")
                .description("Get the current weather for a given city")
                .parameters(
                        FunctionParameters.builder()
                                .putAdditionalProperty("type", JsonValue.from("object"))
                                .putAdditionalProperty("properties", JsonValue.from(Map.of(
                                        "city", Map.of(
                                                "type", "string",
                                                "description", "The name of the city, e.g. Paris, London, New York"
                                        ),
                                        "unit", Map.of(
                                                "type", "string",
                                                "enum", List.of("celsius", "fahrenheit"),
                                                "description", "The temperature unit to use (optional, defaults to celsius)"
                                        )
                                )))
                                .putAdditionalProperty("required", JsonValue.from(List.of("city")))
                                .build()
                )
                .build();
    }
}
