package spring.beautiq.domain.product.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import spring.beautiq.domain.product.dto.ai.request.ProductAIRequest;
import spring.beautiq.domain.product.dto.ai.response.ProductAIResponse;
import spring.beautiq.domain.product.dto.common.ProductFilters;
import spring.beautiq.domain.product.dto.request.ProductRequest;
import spring.beautiq.domain.product.dto.response.ProductResponse;
import spring.beautiq.domain.product.entity.ProductEntity;
import spring.beautiq.domain.product.exception.ProductExceptions;
import spring.beautiq.domain.product.repository.ProductRepository;
import spring.beautiq.domain.product.wishlist.dto.common.WishlistOrderOption;
import spring.beautiq.domain.product.wishlist.dto.response.WishProductResponse;
import spring.beautiq.domain.product.wishlist.entity.WishlistProductEntity;
import spring.beautiq.domain.product.wishlist.repository.WishlistProductRepository;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysisEntity;
import spring.beautiq.domain.skinanalysis.exception.SkinAnalysisExceptions;
import spring.beautiq.domain.skinanalysis.repository.SkinAnalysisRepository;
import spring.beautiq.domain.user.repository.UserRepository;
import spring.beautiq.global.exception.GlobalErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final WishlistProductRepository wishlistProductRepository;
    private final UserRepository userRepository;
    private final SkinAnalysisRepository skinAnalysisRepository;
    private final WebClient.Builder webClientBuilder;

    @Transactional(readOnly = true)
    public ProductResponse getRecommendProducts(UUID userId, UUID analysisId, ProductRequest request) {
        // 피부 분석 조회 및 권한 검증
        SkinAnalysisEntity analysis = skinAnalysisRepository.findById(analysisId)
                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);

        if (!analysis.getUser().getId().equals(userId)) {
            throw SkinAnalysisExceptions.SKIN_ANALYSIS_FORBIDDEN.toException();
        }

        // 카테고리 매핑
        List<String> categories = request.getCategories().stream()
                .map(cat -> cat.name().toLowerCase())
                .collect(Collectors.toList());

        // 제품 필터링 Specification 생성
        Specification<ProductEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 카테고리 필터
            if (request.getCategories() != null && !request.getCategories().isEmpty()) {
                predicates.add(root.get("category").in(categories));
            }

            // 가격, 리뷰 필터
            ProductFilters filters = request.getFilters();
            if (filters != null) {
                addPricePredicates(root, cb, filters.getPrice(), predicates);
                addReviewScorePredicate(root, cb, filters.getReviewScore(), predicates);
                addReviewCountPredicate(root, cb, filters.getReviewCount(), predicates);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 정렬 옵션 생성
        Sort sort = (request.getSort() == null || request.getSort().getBy() == null)
                ? Sort.by(Sort.Direction.DESC, "reviewScore")
                : Sort.by("asc".equalsIgnoreCase(request.getSort().getOrder())
                        ? Sort.Direction.ASC : Sort.Direction.DESC,
                request.getSort().getBy());

        // 제품 조회 및 제한
        List<ProductEntity> products = productRepository.findAll(spec, sort)
                .stream()
                .limit(request.getTopN())
                .collect(Collectors.toList());

        // AI 서버 호출
        ProductAIRequest aiRequest = ProductAIRequest.from(analysis, categories, products);
        ProductAIResponse aiResponse = webClientBuilder.build()
                .post()
                .uri("/product/recommend")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(aiRequest)
                .retrieve()
                .bodyToMono(ProductAIResponse.class)
                .block();

        if (aiResponse == null || !"success".equals(aiResponse.getStatus())) {
            throw ProductExceptions.AI_SERVER_INVALID_RESPONSE.toException();
        }

        // 추천 결과 매핑
        List<ProductResponse.ProductRecommendation> recommendations = aiResponse.getRecommendations().stream()
                .map(aiRec -> {
                    ProductEntity product = products.stream()
                            .filter(p -> p.getId().toString().equals(aiRec.getProductId()))
                            .findFirst()
                            .orElseThrow(ProductExceptions.PRODUCT_NOT_IN_FILTERED_LIST::toException);
                    return ProductResponse.ProductRecommendation.of(product, aiRec.getReason());
                })
                .collect(Collectors.toList());

        return ProductResponse.from(recommendations);
    }

    @Transactional
    public WishProductResponse addWishlist(UUID userId, UUID productId) {
        if (wishlistProductRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
            throw ProductExceptions.WISHLIST_ALREADY_EXISTS.toException();
        }

        WishlistProductEntity saved = wishlistProductRepository.save(
                WishlistProductEntity.builder()
                        .user(userRepository.findById(userId)
                                .orElseThrow(GlobalErrorCode.SECURITY_USER_NOT_FOUND::toException))
                        .product(productRepository.findById(productId)
                                .orElseThrow(ProductExceptions.PRODUCT_NOT_FOUND::toException))
                        .build()
        );

        return WishProductResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<WishProductResponse> getAllWishProduct(UUID userId, WishlistOrderOption order, int page, int size) {
        Sort sort = switch (order) {
            case RATE -> Sort.by(Sort.Order.desc("product.reviewScore"));
            case POPULAR -> Sort.by(Sort.Order.desc("product.reviewCount"));
            case NEWEST -> Sort.by(Sort.Order.desc("createdAt"));
        };

        return wishlistProductRepository.findAllByUser_Id(userId, PageRequest.of(page, size, sort))
                .map(WishProductResponse::from);
    }

    @Transactional(readOnly = true)
    public WishProductResponse getWishProduct(UUID userId, UUID productId) {
        return wishlistProductRepository.findByUser_IdAndProduct_Id(userId, productId)
                .map(WishProductResponse::from)
                .orElseThrow(ProductExceptions.WISHLIST_NOT_FOUND::toException);
    }

    @Transactional
    public void deleteWishlist(UUID userId, UUID productId) {
        if (!wishlistProductRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
            throw ProductExceptions.WISHLIST_NOT_FOUND.toException();
        }
        wishlistProductRepository.deleteByUser_IdAndProduct_Id(userId, productId);
    }

    // Specification 헬퍼 메서드들
    private void addPricePredicates(Root<ProductEntity> root, CriteriaBuilder cb,
                                    ProductFilters.PriceFilter price, List<Predicate> predicates) {
        if (price == null) return;

        Optional.ofNullable(price.getMin())
                .ifPresent(min -> predicates.add(cb.greaterThanOrEqualTo(root.get("salePrice"), min)));
        Optional.ofNullable(price.getMax())
                .ifPresent(max -> predicates.add(cb.lessThanOrEqualTo(root.get("salePrice"), max)));
    }

    private void addReviewScorePredicate(Root<ProductEntity> root, CriteriaBuilder cb,
                                         ProductFilters.ReviewScoreFilter reviewScore, List<Predicate> predicates) {
        if (reviewScore == null) return;

        Optional.ofNullable(reviewScore.getMin())
                .ifPresent(min -> predicates.add(cb.greaterThanOrEqualTo(root.get("reviewScore"), min)));
    }

    private void addReviewCountPredicate(Root<ProductEntity> root, CriteriaBuilder cb,
                                         ProductFilters.ReviewCountFilter reviewCount, List<Predicate> predicates) {
        if (reviewCount == null) return;

        Optional.ofNullable(reviewCount.getMin())
                .ifPresent(min -> predicates.add(cb.greaterThanOrEqualTo(root.get("reviewCount"), min)));
    }
}
