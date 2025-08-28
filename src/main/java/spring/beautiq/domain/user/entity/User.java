package spring.beautiq.domain.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.*;
import spring.beautiq.domain.user.AuthProvider.AuthProvider;
import spring.beautiq.global.base.BaseEntity;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    //Google, kako
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    // 구글: sub, 카카오: id
    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;

    @Column(unique = true)
    private String email;
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles;

    private String refreshToken;
}

