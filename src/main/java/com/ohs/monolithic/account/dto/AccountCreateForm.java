package com.ohs.monolithic.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCreateForm {
    @Size(min = 3, max = 25)
    @NotEmpty(message = "ID는 필수항목입니다.")
    private String username;

    @Size(min = 3, max = 25)
    @NotEmpty(message = "닉네임은 필수항목입니다.")
    private String nickname;

    @Size(min = 7, max = 25)
    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password;

    @Size(min = 7, max = 25)
    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String password2;

    //@NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String email;
}