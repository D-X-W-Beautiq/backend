package spring.beautiq.domain.makeup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.beautiq.global.base.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MakeUp extends BaseEntity {

    private String keywords;

    private Boolean isLiked;

    // todo: 유저, 피부분석 매핑

    public Boolean changeWish() {
        this.isLiked = !this.isLiked;
        return this.isLiked;
    }
}
