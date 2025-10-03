package spring.beautiq.domain.product.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.product.entity.ProductEntity;
import spring.beautiq.domain.product.exception.ProductExceptions;
import spring.beautiq.global.exception.ApiException;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "제품 상세 정보",
        example = """
                {
                  "id": "550e8400-e29b-41d4-a716-446655440000",
                  "category": "스킨케어",
                  "overallRank": 1,
                  "pageNumber": 1,
                  "pageRank": 1,
                  "brand": "라운드랩",
                  "productName": "1025 독도 토너",
                  "listPrice": 20000,
                  "salePrice": 15000,
                  "reviewScore": 4.8,
                  "reviewCount": 1234,
                  "ingredients": "정제수, 글리세린, 부틸렌글라이콜, 판테놀, 해조추출물",
                  "description": "민감한 피부를 진정시키는 토너",
                  "tags": "민감성피부, 진정, 보습",
                  "bestOrNew": "BEST",
                  "imageUrl": "https://image.oliveyoung.co.kr/uploads/images/goods/400/10/0000/0018/A00000018321012ko.jpg",
                  "productUrl": "https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183210"
                }
                """
)
public class Product {

    @NotNull
    @Schema(
            description = "제품 고유 ID",
            example = "550e8400-e29b-41d4-a716-446655440000",
            type = "string",
            format = "uuid",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String id;

    @Schema(
            description = "제품 카테고리",
            example = "스킨케어",
            type = "string",
            maxLength = 50
    )
    private String category;

    @Schema(
            description = "전체 순위 (올리브영 전체 랭킹)",
            example = "1",
            type = "integer",
            format = "int32",
            minimum = "1"
    )
    private Integer overallRank;

    @Schema(
            description = "페이지 번호",
            example = "1",
            type = "integer",
            format = "int32",
            minimum = "1"
    )
    private Integer pageNumber;

    @Schema(
            description = "페이지 내 순위",
            example = "1",
            type = "integer",
            format = "int32",
            minimum = "1"
    )
    private Integer pageRank;

    @Schema(
            description = "브랜드명",
            example = "라운드랩",
            type = "string",
            maxLength = 100
    )
    private String brand;

    @NotNull
    @Schema(
            description = "제품명",
            example = "1025 독도 토너",
            type = "string",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String productName;

    @Schema(
            description = "정가 (원)",
            example = "20000",
            type = "integer",
            format = "int32",
            minimum = "0"
    )
    private Integer listPrice;

    @Schema(
            description = "판매가 (원)",
            example = "15000",
            type = "integer",
            format = "int32",
            minimum = "0"
    )
    private Integer salePrice;

    @Schema(
            description = "리뷰 평점 (0.0-5.0)",
            example = "4.8",
            type = "number",
            format = "float",
            minimum = "0.0",
            maximum = "5.0"
    )
    private Float reviewScore;

    @Schema(
            description = "리뷰 개수",
            example = "1234",
            type = "integer",
            format = "int32",
            minimum = "0"
    )
    private Integer reviewCount;

    @Schema(
            description = "제품 성분 목록",
            example = "정제수, 글리세린, 부틸렌글라이콜, 판테놀, 해조추출물",
            type = "string"
    )
    private String ingredients;

    @Schema(
            description = "제품 설명",
            example = "민감한 피부를 진정시키는 토너",
            type = "string"
    )
    private String description;

    @Schema(
            description = "제품 태그 (쉼표로 구분)",
            example = "민감성피부, 진정, 보습",
            type = "string",
            maxLength = 500
    )
    private String tags;

    @Schema(
            description = "베스트/신제품 구분",
            example = "BEST",
            type = "string",
            maxLength = 50,
            allowableValues = {"BEST", "NEW", ""}
    )
    private String bestOrNew;

    @Schema(
            description = "제품 이미지 URL",
            example = "https://image.oliveyoung.co.kr/uploads/images/goods/400/10/0000/0018/A00000018321012ko.jpg",
            type = "string",
            format = "uri",
            maxLength = 500
    )
    private String imageUrl;

    @Schema(
            description = "제품 상세 페이지 URL",
            example = "https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183210",
            type = "string",
            format = "uri",
            maxLength = 500
    )
    private String productUrl;

    public static Product from(ProductEntity entity) {
        if (entity == null || entity.getId() == null || entity.getProductName() == null) {
            throw new ApiException(ProductExceptions.PRODUCT_NOT_FOUND);
        }
        return Product.builder()
                .id(entity.getId().toString())
                .category(entity.getCategory())
                .overallRank(entity.getOverallRank())
                .pageNumber(entity.getPageNumber())
                .pageRank(entity.getPageRank())
                .brand(entity.getBrand())
                .productName(entity.getProductName())
                .listPrice(entity.getListPrice())
                .salePrice(entity.getSalePrice())
                .reviewScore(entity.getReviewScore() != null ? entity.getReviewScore().floatValue() : 0.0f)
                .reviewCount(entity.getReviewCount())
                .ingredients(entity.getIngredients())
                .description(entity.getDescription())
                .tags(entity.getTags())
                .bestOrNew(entity.getBestOrNew())
                .imageUrl(entity.getImageUrl())
                .productUrl(entity.getProductUrl())
                .build();
    }
}
