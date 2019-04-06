package io.coster.usermanagementsvc.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {

    @Email
    private String userId;

    @Length(min = 5, max = 50)
    private String password;

    private String token;

}
