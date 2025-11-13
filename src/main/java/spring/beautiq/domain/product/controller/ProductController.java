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
import spring.beautiq.domain.product.wishlist.dto.response.WishlistToggleResponse;
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
                                                  "product": {
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
                                                    "imageUrl": "https://example.com/image.jpg",
                                                    "productUrl": "https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183210"
                                                  },
                                                  "reason": "귀하의 민감성 피부 타입에 적합한 진정 성분이 포함되어 있습니다.",
                                                  "isWish": false
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 필터 조건 오류 또는 필수 필드 누락",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "필수 필드 누락",
                                    value = """
                                            {
                                              "code": "BAD_REQUEST",
                                              "message": "topN 필드는 필수입니다.",
                                              "status": 400,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - JWT 토큰이 없거나 유효하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                                            {
                                              "code": "UNAUTHORIZED",
                                              "message": "인증이 필요합니다.",
                                              "status": 401,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 다른 사용자의 분석 결과에 접근",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                            {
                                              "code": "SKIN_ANALYSIS_FORBIDDEN",
                                              "message": "해당 피부 분석 결과에 접근할 권한이 없습니다.",
                                              "status": 403,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "피부 분석 결과를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "분석 결과 없음",
                                    value = """
                                            {
                                              "code": "SKIN_ANALYSIS_NOT_FOUND",
                                              "message": "피부 분석 결과를 찾을 수 없습니다.",
                                              "status": 404,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - AI 서버 통신 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "AI 서버 오류",
                                    value = """
                                            {
                                              "code": "AI_SERVER_INVALID_RESPONSE",
                                              "message": "AI 서버 응답이 올바르지 않습니다.",
                                              "status": 500,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/skin-analysis/{analysisId}/recommend-products")
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
            summary = "위시리스트 토글 (추가/삭제 통합)",
            description = """
                    제품의 위시리스트 상태를 토글합니다. (프론트엔드 편의 기능)
                    
                    **동작 방식:**
                    - 위시리스트에 **없으면 → 추가**
                    - 위시리스트에 **있으면 → 삭제**
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "토글 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WishlistToggleResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "추가된 경우",
                                            value = """
                                                    {
                                                      "productId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                                      "isWish": true,
                                                      "message": "위시리스트에 추가되었습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "삭제된 경우",
                                            value = """
                                                    {
                                                      "productId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                                      "isWish": false,
                                                      "message": "위시리스트에서 제거되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "UNAUTHORIZED",
                                              "message": "인증이 필요합니다.",
                                              "status": 401,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "제품을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "PRODUCT_NOT_FOUND",
                                              "message": "제품을 찾을 수 없습니다.",
                                              "status": 404,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/products/{productId}/wishlists")
    public ResponseEntity<WishlistToggleResponse> toggleWishlist(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(description = "제품 ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable("productId") UUID productId
    ) {
        boolean isWish = productService.toggleWishlist(userId, productId);
        return ResponseEntity.ok(WishlistToggleResponse.of(productId, isWish));
    }

    @Operation(
            summary = "위시리스트 전체 조회",
            description = """
                    사용자의 위시리스트에 담긴 모든 제품을 조회합니다.
                    
                    **정렬 옵션:**
                    - rate: 평점 높은 순
                    - popular: 인기순 (리뷰 많은 순)
                    - newest: 최신 추가순 (기본)
                    
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
                            schema = @Schema(implementation = Page.class),
                            examples = @ExampleObject(
                                    name = "위시리스트 목록",
                                    value = """
                                            {
                                              "content": [
                                                {
                                                  "id": "660e8400-e29b-41d4-a716-446655440001",
                                                  "userId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
                                                  "wishProduct": {
                                                    "productId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                                    "category": "스킨케어",
                                                    "overallRank": 1,
                                                    "pageNumber": 1,
                                                    "pageRank": 1,
                                                    "brand": "라운드랩",
                                                    "productName": "[라운드랩] 1025 독도 토너 200ml",
                                                    "listPrice": 30000,
                                                    "salePrice": 25000,
                                                    "reviewScore": 4.5,
                                                    "reviewCount": 1234,
                                                    "ingredients": "정제수, 글리세린, 부틸렌글라이콜",
                                                    "description": "독도 해양심층수로 피부를 진정시키는 토너",
                                                    "tags": "민감성피부, 진정, 보습",
                                                    "bestOrNew": "BEST",
                                                    "imageUrl": "https://example.com/image.jpg",
                                                    "productUrl": "https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183210"
                                                  }
                                                }
                                              ],
                                              "pageable": {
                                                "pageNumber": 0,
                                                "pageSize": 10
                                              },
                                              "totalElements": 15,
                                              "totalPages": 2,
                                              "last": false
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                                            {
                                              "code": "UNAUTHORIZED",
                                              "message": "인증이 필요합니다.",
                                              "status": 401,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/users/me/wishlists/products")
    public ResponseEntity<Page<WishProductResponse>> getAllWishProduct(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(
                    description = "정렬 옵션",
                    schema = @Schema(allowableValues = {"rate", "popular", "newest"}),
                    example = "rate"
            )
            @RequestParam(name = "order", defaultValue = "rate") String order,
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
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WishProductResponse.class),
                            examples = @ExampleObject(
                                    name = "위시리스트 제품 상세",
                                    value = """
                                            {
                                              "id": "660e8400-e29b-41d4-a716-446655440001",
                                              "userId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
                                              "wishProduct": {
                                                "productId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                                                "category": "스킨케어",
                                                "overallRank": 1,
                                                "pageNumber": 1,
                                                "pageRank": 1,
                                                "brand": "라운드랩",
                                                "productName": "[라운드랩] 1025 독도 토너 200ml",
                                                "listPrice": 30000,
                                                "salePrice": 25000,
                                                "reviewScore": 4.5,
                                                "reviewCount": 1234,
                                                "ingredients": "정제수, 글리세린, 부틸렌글라이콜",
                                                "description": "독도 해양심층수로 피부를 진정시키는 토너",
                                                "tags": "민감성피부, 진정, 보습",
                                                "bestOrNew": "BEST",
                                                "imageUrl": "https://example.com/image.jpg",
                                                "productUrl": "https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183210"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "UNAUTHORIZED",
                                              "message": "인증이 필요합니다.",
                                              "status": 401,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "위시리스트에 해당 제품이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "WISHLIST_NOT_FOUND",
                                              "message": "위시리스트를 찾을 수 없습니다.",
                                              "status": 404,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/users/me/wishlists/products/{productId}")
    public ResponseEntity<WishProductResponse> getWishProduct(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(description = "제품 ID")
            @PathVariable("productId") UUID productId
    ) {
        return ResponseEntity.ok(productService.getWishProduct(userId, productId));
    }
}
