package spring.beautiq.domain.user.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import spring.beautiq.global.base.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class UserEntity extends BaseEntity {

    @Column(length = 50)
    private String username; // 닉네임, 변경 가능

    @Column(length = 100)
    private String email; // 소셜마다 다를 수 있으므로 nullable

    @Column(length = 50)
    private String name; // 실명

    @Column(length = 20)
    private String role;

    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProviderEntity> providers = new ArrayList<>();
}
