package spring.beautiq.domain.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDto {
    private boolean authenticated;
    private UUID userId;
    private String email;
    private String name;
    private String provider;
}
