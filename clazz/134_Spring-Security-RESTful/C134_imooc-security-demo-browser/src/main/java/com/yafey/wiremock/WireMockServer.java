package com.yafey.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.removeAllMappings;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

public class WireMockServer {

    public static void main(String[] args) throws IOException {
        // 绑定本地服务器
        configureFor(8062);
        // 清理配置
        removeAllMappings();
        
        // 伪造一个 测试桩 （即 服务器如何处理请求）
        stubFor(get(urlPathEqualTo("/order/1"))
                .willReturn(aResponse()
                        .withBody("{\"id\":\"mockid_1\"}")
                        .withStatus(200))
        );
        
        mock("/order/2", "mock/response/order_2.txt");
        mock("/order/3", "mock/response/order_3.txt");

    }

    private static void mock(String url, String file) throws IOException {
        ClassPathResource order1 = new ClassPathResource(file);
        String order1Content = FileUtils.readFileToString(order1.getFile(), Charset.defaultCharset());
        
        stubFor(get(urlPathEqualTo(url))
                .willReturn(aResponse()
                        .withBody(order1Content)
                        .withStatus(200))
        );

    }

}
