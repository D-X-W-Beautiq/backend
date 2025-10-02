package spring.beautiq.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;
import spring.beautiq.global.base.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product")
public class ProductEntity extends BaseEntity {

    @Column(nullable = false)
    private String category;

    @Column
    private Integer overallRank;

    @Column
    private Integer pageNumber;

    @Column
    private Integer pageRank;

    @Column
    private String brand;

    @Column(nullable = false)
    private String productName;

    @Column
    private Integer listPrice;

    @Column
    private Integer salePrice;

    @Column
    private Double reviewScore;

    @Column
    private Integer reviewCount;

    @Column(columnDefinition = "TEXT")
    private String ingredients;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String tags;

    @Column(length = 50)
    private String bestOrNew;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500, nullable = false)
    private String productUrl;
}
