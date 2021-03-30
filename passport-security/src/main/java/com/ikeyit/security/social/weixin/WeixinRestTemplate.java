package com.ikeyit.security.social.weixin;

import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeixinRestTemplate {

    static RestTemplate restTemplate = null;


    public static RestTemplate get() {
        if (restTemplate == null) {
            MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
            List<MediaType> mediaTypes = new ArrayList<MediaType>(jsonConverter.getSupportedMediaTypes());
            mediaTypes.add(MediaType.TEXT_PLAIN);
            jsonConverter.setSupportedMediaTypes(mediaTypes);
            restTemplate = new RestTemplate(Arrays.asList(new FormHttpMessageConverter(), jsonConverter));
        }

        return restTemplate;
    }

}
