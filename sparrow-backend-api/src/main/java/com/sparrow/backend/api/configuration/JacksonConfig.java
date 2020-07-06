package com.sparrow.backend.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparrow.common.JsonUtils;
import org.springframework.context.annotation.Bean;


public class JacksonConfig {

    @Bean(name = "objectMapper")
    public ObjectMapper jacksonObjectMapper() {
        return JsonUtils.getDefaultMapper();
    }

}
