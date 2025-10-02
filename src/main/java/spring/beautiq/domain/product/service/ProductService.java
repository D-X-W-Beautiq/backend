package spring.beautiq.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import spring.beautiq.domain.product.dto.ai.request.ProductAIRequest;
import spring.beautiq.domain.product.dto.common.ProductReqRes;
import spring.beautiq.domain.product.dto.request.ProductRequest;
import spring.beautiq.domain.product.dto.ai.response.ProductAIResponse;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysis;
import spring.beautiq.domain.skinanalysis.exception.SkinAnalysisExceptions;
import spring.beautiq.domain.skinanalysis.repository.SkinAnalysisRepository;
import spring.beautiq.domain.product.entity.ProductEntity;
import spring.beautiq.domain.product.exception.ProductExceptions;
import spring.beautiq.domain.product.repository.ProductRepository;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;

import java.util.UUID;

import spring.beautiq.domain.product.dto.common.Product;
import spring.beautiq.global.exception.GlobalErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import spring.beautiq.domain.product.dto.common.OrderOption;

@Service
@RequiredArgsConstructor
public class ProductService {

//    private final SkinAnalysisRepository skinAnalysisRepository;
//    private final WebClient.Builder webClientBuilder;
//    private final ProductRepository productRepository;
//    private final UserRepository userRepository;
//
//    // AI를 활용한 제품 추천
//    @Transactional(readOnly = true)
//    public ProductAIResponse productsRecommend(
//            UUID userId,
//            UUID analysisId,
//            ProductRequest clientRequest
//    ) {
//        // 1. analysisId로 SkinAnalysis 조회
//        SkinAnalysis analysis = skinAnalysisRepository.findById(analysisId)
//                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);
//
//        // 2. 해당 SkinAnalysis가 userId와 일치하는지 확인
//        if (analysis.getUser() == null || analysis.getUser().getId() == null || !analysis.getUser().getId().equals(userId)) {
//            throw SkinAnalysisExceptions.SKIN_ANALYSIS_FORBIDDEN.toException();
//        }
//
//        // 3. AI 서버에 요청 보내기
//        ProductAIRequest request = ProductAIRequest.builder()
//                .analysis(ProductAIRequest.toAnalysis(analysis))
//                .topN(clientRequest.getTopN())
//                .locale(clientRequest.getLocale())
//                .filters(clientRequest.getFilters())
//                .build();
//
//        // 4. AI 서버 응답 받기
//        ProductAIResponse aiResponse = webClientBuilder.build().post()
//                .uri("/skin/products/recommend")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(request)
//                .retrieve()
//                .bodyToMono(ProductAIResponse.class)
//                .block();
//
//        // 5. 응답이 null이면 예외 처리
//        if (aiResponse == null) {
//            throw SkinAnalysisExceptions.AI_SERVER_RESPONSE_EMPTY.toException();
//        }
//
//        return aiResponse;
//    }
//
//    // Wishlist 제품 추가
//    @Transactional
//    public ProductReqRes addWishlist(UUID userId, ProductReqRes request) {
//        // userId로 User 조회
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(GlobalErrorCode.SECURITY_USER_NOT_FOUND::toException);
//
//        // request에서 제품 정보 추출
//        Product product = request.getRecommendations();
//        if (product == null) {
//            throw ProductExceptions.RECOMMENDATION_REQUIRED.toException();
//        }
//
//        return ProductReqRes.from(
//                productRepository.save(ProductEntity.builder()
//                .user(user)
//                .needs(request.getNeeds())
//                .productName(product.getProductName())
//                .category(product.getCategory())
//                .price(product.getPrice())
//                .reviewCount(product.getReviewCount())
//                .reason(product.getReason())
//                .build()
//            )
//        );
//    }
//
//    // 전체 Wishlist 제품 조회 (페이징, 정렬)
//    @Transactional(readOnly = true)
//    public Page<Product> getAllWishProduct(UUID userId, OrderOption order, int page, int size) {
//
//        Sort sort = switch (order) {
//            case POPULAR -> Sort.by(Sort.Order.desc("reviewCount"));
//            case NEWEST -> Sort.by(Sort.Order.desc("createdAt"));
//        };
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//        Page<ProductEntity> result = productRepository.findAllByUser_Id(userId, pageable);
//
//        return result.map(entity -> Product.builder()
//                .productName(entity.getProductName())
//                .category(entity.getCategory())
//                .price(entity.getPrice())
//                .reviewCount(entity.getReviewCount())
//                .reason(entity.getReason())
//                .build()
//        );
//    }
//
//    // 특정 Wishlist 제품 상세 조회
//    @Transactional(readOnly = true)
//    public ProductReqRes getWishProduct(UUID userId, UUID productId) {
//        ProductEntity entity = productRepository.findByIdAndUser_Id(productId, userId)
//                .orElseThrow(ProductExceptions.WISHLIST_NOT_FOUND::toException);
//
//        return ProductReqRes.builder()
//                .needs(entity.getNeeds())
//                .recommendations(Product.builder()
//                        .productName(entity.getProductName())
//                        .category(entity.getCategory())
//                        .price(entity.getPrice())
//                        .reviewCount(entity.getReviewCount())
//                        .reason(entity.getReason())
//                        .build())
//                .build();
//    }
//
//    // Wishlist 제품 삭제
//    @Transactional
//    public void deleteWishlist(UUID userId, UUID productId) {
//
//        ProductEntity productEntity = productRepository.findByIdAndUser_Id(productId, userId)
//                .orElseThrow(ProductExceptions.WISHLIST_NOT_FOUND::toException);
//
//        productRepository.delete(productEntity);
//    }
}
