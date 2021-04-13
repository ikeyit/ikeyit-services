package com.ikeyit.product.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.server}")
    String server;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(server)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
