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
import java.util.Set;

import java.util.UUID;
import lombok.*;
import spring.beautiq.domain.user.AuthProvider.AuthProvider;
import spring.beautiq.global.base.BaseEntity;

@Entity
@Getter
@Setter
public class UserEntity extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    private String email;
    private String name;
    private String username;
    private String role;

}

