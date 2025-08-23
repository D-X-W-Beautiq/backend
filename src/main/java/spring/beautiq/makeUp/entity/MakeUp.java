package spring.beautiq.makeUp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MakeUp {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keywords;

    private Boolean isLiked;

    @CreatedDate
    private LocalDateTime createTime;

    @LastModifiedDate
    private LocalDateTime updateTime;

    // todo: 유저, 피부분석 매핑

    public void changeWish() {
        this.isLiked = !this.isLiked;
    }
}
