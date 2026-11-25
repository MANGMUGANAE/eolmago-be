package com.mangmuganae.eolmago.infra.impersonator;

import com.github.zhkl0228.impersonator.ImpersonatorApi;
import com.github.zhkl0228.impersonator.ImpersonatorFactory;
import javax.net.ssl.SSLContext;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClientFactory;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

@Service
public class ImpersonatorUtil {

  private final OkHttpClient client;

  //임퍼소네이터가 TLS 협상에 실패하는 서버(구형 ciphersuite 등)를 위한 표준 클라이언트입니다.
  private final OkHttpClient fallbackClient;

  public ImpersonatorUtil() {
    ImpersonatorApi api = ImpersonatorFactory.macSafari();

    OkHttpClientFactory factory = OkHttpClientFactory.create(api);

    this.client = factory.newHttpClient();
    this.fallbackClient = new OkHttpClient();
  }

  public String requestUrl(String url) {

    try {
      Request request = new Request.Builder()
          .url(url)
          .build();

      return client.newCall(request).execute().body().string();
    } catch (Exception e) {
      System.out.println("요청중 에러 : " + e.getMessage());
      return null;
    }
  }

  /**
   * 단축 URL 의 리다이렉트를 끝까지 따라간 최종 URL 을 반환합니다.
   * http / https 가 아닌 주소이거나 요청에 실패하면 null 을 반환합니다.
   */
  public String resolveFinalUrl(String url) {

    String finalUrl = resolveFinalUrl(url, client);

    if (finalUrl == null) {
      finalUrl = resolveFinalUrl(url, fallbackClient);
    }

    return finalUrl;
  }

  private String resolveFinalUrl(String url, OkHttpClient httpClient) {

    try {
      Request request = new Request.Builder()
          .url(url)
          .build();

      try (Response response = httpClient.newCall(request).execute()) {
        return response.request().url().toString();
      }
    } catch (Exception e) {
      System.out.println("최종 URL 확인중 에러 : " + e.getMessage());
      return null;
    }
  }

}
