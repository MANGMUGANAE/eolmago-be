package com.mangmuganae.eolmago.infra.impersonator;

import com.github.zhkl0228.impersonator.ImpersonatorApi;
import com.github.zhkl0228.impersonator.ImpersonatorFactory;
import javax.net.ssl.SSLContext;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClientFactory;
import okhttp3.Request;
import org.springframework.stereotype.Service;

@Service
public class ImpersonatorUtil {

  private final OkHttpClient client;

  public ImpersonatorUtil() {
    ImpersonatorApi api = ImpersonatorFactory.macSafari();

    OkHttpClientFactory factory = OkHttpClientFactory.create(api);

    this.client = factory.newHttpClient();
  }

  public String requestUrl(String url) {

    Request request = new Request.Builder()
        .url(url)
        .build();

    try {
      return client.newCall(request).execute().body().string();
    } catch (Exception e) {
      System.out.println("요청중 에러 : " + e.getMessage());
      return null;
    }
  }

}
