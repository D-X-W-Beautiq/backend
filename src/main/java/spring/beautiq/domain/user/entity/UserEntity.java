package spring.beautiq.domain.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import spring.beautiq.global.base.BaseEntity;

@Entity
@Getter
@Setter
public class UserEntity extends BaseEntity {

    @Column(name = "auth_key", unique = true, nullable = false)
    private String authKey; // 기존 username -> authKey (provider_providerId)
    private String username;
    private String email;
    private String name;
    private String role;
}
