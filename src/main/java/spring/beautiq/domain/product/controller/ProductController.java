package spring.beautiq.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.beautiq.domain.product.dto.request.ProductRequest;
import spring.beautiq.domain.product.dto.response.ProductResponse;
import spring.beautiq.domain.product.service.ProductService;
import spring.beautiq.domain.product.wishlist.dto.common.WishlistOrderOption;
import spring.beautiq.domain.product.wishlist.dto.response.WishProductResponse;
import spring.beautiq.global.security.annotation.CurrentUserId;
import spring.beautiq.global.security.guard.MemberGuard;

import java.util.UUID;

@MemberGuard
@RestController
@RequiredArgsConstructor
@Tag(name = "Product", description = "제품 추천 및 위시리스트 관리 API")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "피부 분석 기반 제품 추천",
            description = """
                    피부 분석 결과를 기반으로 사용자에게 맞는 제품을 추천합니다.
                    
                    **흐름:**
                    1. 피부 분석 ID와 카테고리 정보 입력
                    2. 분석 결과의 피부 타입/문제점에 맞는 제품 필터링
                    3. 올리브영 데이터셋에서 적합한 제품 추천
                    
                    **요청 데이터:**
                    - analysisId: 피부 분석 ID
                    - category: 제품 카테고리 (스킨케어/메이크업 등)
                    - skinConcern: 주요 피부 고민 (선택)
                    
                    **추천 기준:**
                    - 피부 타입 매칭
                    - 피부 문제점(수분/유분/색소침착 등) 개선 제품
                    - 평점 및 리뷰 수 고려
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "추천 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class),
                            examples = @ExampleObject(
                                    name = "추천 제품 목록",
                                    value = """
                                            {
                                              "products": [
                                                {
                                                  "productId": "550e8400-e29b-41d4-a716-446655440000",
                                                  "name": "토너 패드",
                                                  "brand": "메디힐",
                                                  "category": "스킨케어",
                                                  "price": 15000,
                                                  "rating": 4.5,
                                                  "reviewCount": 1234,
                                                  "imageUrl": "https://...",
                                                  "description": "수분 공급에 효과적인 토너 패드"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 분석 ID 누락 또는 카테고리 오류"),
            @ApiResponse(responseCode = "404", description = "피부 분석 결과를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/skin-analyses/{analysisId}/recommend-products")
    public ResponseEntity<ProductResponse> getRecommendProducts(
            @Parameter(description = "피부 분석 ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable("analysisId") UUID analysisId,
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "제품 추천 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductRequest.class),
                            examples = @ExampleObject(
                                    name = "추천 요청 예시",
                                    value = """
                                            {
                                              "category": "skincare",
                                              "skinConcern": "hydration",
                                              "limit": 10
                                            }
                                            """
                            )
                    )
            )
            ProductRequest request
    ) {
        return ResponseEntity.ok(productService.getRecommendProducts(userId, analysisId, request));
    }

    @Operation(
            summary = "위시리스트에 제품 추가",
            description = """
                    특정 제품을 사용자의 위시리스트에 추가합니다.
                    
                    **기능:**
                    - 중복 추가 방지
                    - 추가된 제품 정보 반환
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "추가 성공",
                    content = @Content(schema = @Schema(implementation = WishProductResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "제품을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 위시리스트에 존재함")
    })
    @PostMapping("/products/{productId}/wishlists")
    public ResponseEntity<WishProductResponse> addWishlist(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(description = "제품 ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable("productId") UUID productId
    ) {
        return ResponseEntity.ok(productService.addWishlist(userId, productId));
    }

    @Operation(
            summary = "위시리스트 전체 조회",
            description = """
                    사용자의 위시리스트에 담긴 모든 제품을 조회합니다.
                    
                    **정렬 옵션:**
                    - newest: 최신 추가순 (기본)
                    - oldest: 오래된 순
                    - price_high: 가격 높은 순
                    - price_low: 가격 낮은 순
                    - rating: 평점 높은 순
                    
                    **페이징:**
                    - page: 페이지 번호 (1부터 시작, 기본: 1)
                    - 페이지당 10개 제품 반환
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            )
    })
    @GetMapping("/users/me/wishlist/products")
    public ResponseEntity<Page<WishProductResponse>> getAllWishProduct(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(
                    description = "정렬 옵션",
                    schema = @Schema(allowableValues = {"newest", "oldest", "price_high", "price_low", "rating"}),
                    example = "newest"
            )
            @RequestParam(name = "order", defaultValue = "newest") String order,
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(name = "page", defaultValue = "1") Integer page
    ) {
        WishlistOrderOption orderOption = WishlistOrderOption.fromParam(order);
        int pageIndex = (page == null || page < 1) ? 0 : page - 1; // 1페이지부터 입력, 0-based로 변환
        return ResponseEntity.ok(productService.getAllWishProduct(userId, orderOption, pageIndex, 10));
    }

    @Operation(
            summary = "위시리스트 특정 제품 조회",
            description = "위시리스트에 담긴 특정 제품의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "위시리스트에 해당 제품이 없음")
    })
    @GetMapping("/users/me/wishlist/products/{productId}")
    public ResponseEntity<WishProductResponse> getWishProduct(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(description = "제품 ID")
            @PathVariable("productId") UUID productId
    ) {
        return ResponseEntity.ok(productService.getWishProduct(userId, productId));
    }

    @Operation(
            summary = "위시리스트에서 제품 삭제",
            description = "위시리스트에서 특정 제품을 제거합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "위시리스트에 해당 제품이 없음")
    })
    @DeleteMapping("/users/me/wishlist/products/{productId}")
    public ResponseEntity<Void> deleteWishlist(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(description = "제품 ID")
            @PathVariable("productId") UUID productId
    ) {
        productService.deleteWishlist(userId, productId);
        return ResponseEntity.noContent().build();
    }
}
