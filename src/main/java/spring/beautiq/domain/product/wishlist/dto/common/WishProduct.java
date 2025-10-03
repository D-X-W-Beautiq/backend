package spring.beautiq.domain.product.wishlist.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "위시리스트 제품 정보")
public class WishProduct {

    @NotNull
    @Schema(description = "브랜드명", example = "라운드랩")
    private String brand;

    @NotNull
    @Schema(description = "제품명", example = "[라운드랩] 1025 독도 토너 200ml")
    private String productName;

    @NotNull
    @Schema(description = "정가", example = "30000")
    private Integer listPrice;

    @NotNull
    @Schema(description = "판매가", example = "25000")
    private Integer salePrice;

    @NotNull
    @Schema(description = "리뷰 평점", example = "4.5")
    private Float reviewScore;

    @NotNull
    @Schema(description = "리뷰 개수", example = "1234")
    private Integer reviewCount;

    @NotNull
    @Schema(description = "제품 설명", example = "독도 해양심층수로 피부를 진정시키는 토너")
    private String description;

    @NotNull
    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;
}
