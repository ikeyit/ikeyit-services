package com.ikeyit.trade.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ikeyit.trade.dto.OrderParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BuyerOrderServiceTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;

    private String authUrl = "http://localhost:8081/auth/login";

    private String myUrl;

    private String authorizationToken;


    @BeforeEach
    void beforeAll() {
        System.out.println("beforeAll");
        myUrl = "http://localhost:" + port + "/";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "ikeyit");
        params.add("password", "test");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<JsonNode> response = testRestTemplate.postForEntity(authUrl,
                request,
                JsonNode.class);
        authorizationToken = response.getHeaders().getFirst("Authorization");
    }

    HttpEntity httpEntity(Object object) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        requestHeaders.add("Authorization", authorizationToken);

        return new HttpEntity<>(object, requestHeaders);
    }

    @Test
    void createOrder() {
        System.out.println("createOrder");
        OrderParam orderParam = new OrderParam();
        orderParam.setPayWay("weixinpay");
        orderParam.setBuyerMemo("hi");
        orderParam.setAddressId(12L);
        orderParam.setCartItemIds(new Long[]{1L,3L});
        ResponseEntity<JsonNode> response =testRestTemplate.postForEntity(myUrl + "order", httpEntity(orderParam), JsonNode.class);
        System.out.println(response);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

    }

    @Test
    void deleteOrder() {
        System.out.println("deleteOrder");
        System.out.println("hi");
    }

}