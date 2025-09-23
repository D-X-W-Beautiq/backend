package spring.beautiq.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import spring.beautiq.domain.product.dto.ai.response.RecommendProductAIResponse;
import spring.beautiq.domain.product.dto.common.RecommendProduct;
import spring.beautiq.domain.product.dto.request.ProductRecommendRequest;
import spring.beautiq.domain.product.dto.common.ProductRecommendReqRes;
import spring.beautiq.domain.product.service.ProductService;
import spring.beautiq.global.security.guard.MemberGuard;

import java.util.UUID;

import spring.beautiq.domain.product.dto.common.ProductOrder;

@MemberGuard
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/skin-analyses/{analysisId}/recommend-products")
    public ResponseEntity<RecommendProductAIResponse> productsRecommend(
            @PathVariable("analysisId") UUID analysisId,
            @AuthenticationPrincipal OAuth2User principal,
            @Valid @RequestBody ProductRecommendRequest request
    ) {

        UUID userId = principal.getAttribute("userId");

        return ResponseEntity.ok(productService.productsRecommend(userId, analysisId, request));
    }

    @PostMapping("/skin-analyses/{analysisId}/recommend-products/wishlists")
    public ResponseEntity<ProductRecommendReqRes> addWishlist(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable String analysisId,
            @Valid @RequestBody ProductRecommendReqRes request
    ) {

        UUID userId = principal.getAttribute("userId");

        ProductRecommendReqRes created = productService.addWishlist(userId, request);

        return ResponseEntity.ok(created);
    }

    @GetMapping("/users/me/wishlist/products")
    public ResponseEntity<Page<RecommendProduct>> getAllWishProduct(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam(name = "order", defaultValue = "newest") String order,
            @RequestParam(name = "page", defaultValue = "1") Integer page
    ) {

        UUID userId = principal.getAttribute("userId");

        ProductOrder productOrder = ProductOrder.fromParam(order);

        int pageIndex = (page == null || page < 1) ? 0 : page - 1; // 1페이지부터 입력, 0-based로 변환

        return ResponseEntity.ok(productService.getAllWishProduct(userId, productOrder, pageIndex, 10));
    }

    @GetMapping("/users/me/wishlist/products/{productId}")
    public ResponseEntity<ProductRecommendReqRes> getWishProduct(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable("productId") UUID productId
    ) {

        UUID userId = principal.getAttribute("userId");

        return ResponseEntity.ok(productService.getWishProduct(userId, productId));
    }

    @DeleteMapping("/users/me/wishlist/products/{productId}")
    public ResponseEntity<Void> deleteWishlist(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable("productId") UUID productId
    ) {

        UUID userId = principal.getAttribute("userId");

        productService.deleteWishlist(userId, productId);

        return ResponseEntity.noContent().build();
    }
}
