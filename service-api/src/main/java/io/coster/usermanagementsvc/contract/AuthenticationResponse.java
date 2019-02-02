package io.coster.usermanagementsvc.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private boolean valid;
    private String userId;
    private String authToken;

}
