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
    @Schema(
            description = "제품 고유 ID",
            example = "7c9e6679-7425-40de-944b-e07fc1f90ae7",
            type = "string",
            format = "uuid",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String productId;

    @NotNull
    @Schema(
            description = "제품명",
            example = "[라운드랩] 1025 독도 토너 200ml",
            type = "string",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String productName;

    @Schema(
            description = "브랜드명",
            example = "라운드랩",
            type = "string"
    )
    private String brand;

    @Schema(
            description = "정가",
            example = "30000",
            type = "integer",
            format = "int32"
    )
    private Integer listPrice;

    @Schema(
            description = "판매가",
            example = "25000",
            type = "integer",
            format = "int32"
    )
    private Integer salePrice;

    @Schema(
            description = "리뷰 평점",
            example = "4.5",
            type = "number",
            format = "float"
    )
    private Float reviewScore;

    @Schema(
            description = "리뷰 개수",
            example = "1234",
            type = "integer",
            format = "int32"
    )
    private Integer reviewCount;

    @Schema(
            description = "제품 설명",
            example = "독도 해양심층수로 피부를 진정시키는 토너",
            type = "string"
    )
    private String description;

    @Schema(
            description = "이미지 URL",
            example = "https://example.com/image.jpg",
            type = "string",
            format = "uri"
    )
    private String imageUrl;
}
