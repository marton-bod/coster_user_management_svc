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
public class RegistrationRequest {

    @Email
    private String emailAddr;

    @Length(min = 2, max = 50)
    private String firstName;

    @Length(min = 2, max = 50)
    private String lastName;

    @Length(min = 6, max = 50)
    private String password;

}
