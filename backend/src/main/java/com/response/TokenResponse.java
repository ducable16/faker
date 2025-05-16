package com.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
        this.refreshToken = null;
    }
}
