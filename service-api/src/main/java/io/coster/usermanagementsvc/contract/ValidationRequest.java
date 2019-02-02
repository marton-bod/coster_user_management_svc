package io.coster.usermanagementsvc.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRequest {

    @Length(min = 2, max = 60)
    private String userId;

    @Length(min = 5)
    private String authToken;

}
