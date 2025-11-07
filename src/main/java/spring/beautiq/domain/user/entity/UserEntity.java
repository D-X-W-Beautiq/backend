package spring.beautiq.domain.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import spring.beautiq.global.base.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "user")
public class UserEntity extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(unique = true)
    private String providerId; // google_123456, kakao_789012 등 OAuth provider의 고유 ID

    private String role;
    @Column(length = 2048)
    private String profileImage;

}
