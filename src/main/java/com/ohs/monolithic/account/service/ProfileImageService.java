package com.ohs.monolithic.account.service;


import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.account.dto.ProfileImageUploadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProfileImageService {
  //final private S3Client s3Client;
  final private Optional<S3Presigner> s3Presigner;

  public ProfileImageUploadDto.Response getPresignedUrl(Long accountId, ProfileImageUploadDto.Request request, AppUser user){
    ProfileImageUploadDto.Response response = new ProfileImageUploadDto.Response();


    String keyName =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + accountId ;
    try{
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] hash = digest.digest(keyName.getBytes());
      keyName = new BigInteger(1, hash).toString(16);
    }catch(Exception e){
      System.out.println("md5 not found");
    }

    response.presignedUrl = getPresignedUrl(keyName, request.contentType, request.contentLength,user);
    response.profileKey = keyName;

    return response;
  }

  String getPresignedUrl(String keyName, String contentType, Long contentLength, AppUser user){

    String bucketName = "ohsite";

    PutObjectRequest request = PutObjectRequest.builder()
            .contentLength(contentLength) //를 이용하여, 크기 제한도 서명이 되는지 확인 필요함.
            .bucket(bucketName)
            .key(String.format("profile/%s", keyName))
            .contentType(contentType)
            .build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .putObjectRequest(request)
            .signatureDuration(Duration.ofMinutes(5))
            .build();

    return s3Presigner.get().presignPutObject(presignRequest).url().toString();
  }

}
