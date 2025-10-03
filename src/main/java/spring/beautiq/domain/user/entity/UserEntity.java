package spring.beautiq.domain.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import spring.beautiq.global.base.BaseEntity;

@Entity
@Getter
@Setter
public class UserEntity extends BaseEntity {

    private String email;
    @Column(unique = true, nullable = false)
    private String username;
    private String role;
    private String profileImage;

}

