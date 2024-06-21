package com.ohs.monolithic.account.dto;

import lombok.AllArgsConstructor;
import lombok.Setter;


public class ProfileImageUploadDto {

  @Setter
  public static class Request{
    public Long contentLength;
    public String contentType;

  }

  public static class Response{
    public String presignedUrl;
    public String profileKey;

  }

}
