package com.mangmuganae.eolmago.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractJacksonHttpMessageConverter;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class MultipartJacksonHttpMessageConverter
    extends AbstractJacksonHttpMessageConverter<JsonMapper> {

  public MultipartJacksonHttpMessageConverter(JsonMapper jsonMapper) {
    super(jsonMapper, MediaType.APPLICATION_OCTET_STREAM);
  }

  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    return false;
  }

  @Override
  public boolean canWrite(ResolvableType type, Class<?> valueClass, MediaType mediaType) {
    return false;
  }

  @Override
  protected boolean canWrite(MediaType mediaType) {
    return false;
  }
}