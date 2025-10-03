package spring.beautiq.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import spring.beautiq.global.base.BaseEntity;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(
    name = "user_provider",
    uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
)
public class UserProviderEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 50)
    private String provider; // google, kakao 등

    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId; // 소셜에서 발급한 고유 ID
}

