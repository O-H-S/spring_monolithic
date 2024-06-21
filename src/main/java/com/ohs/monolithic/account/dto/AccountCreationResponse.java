package com.ohs.monolithic.account.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AccountCreationResponse {
  final AccountResponse userData;
}
