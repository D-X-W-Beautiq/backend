package spring.beautiq.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import spring.beautiq.domain.product.dto.ai.response.ProductAIResponse;
import spring.beautiq.domain.product.dto.common.Product;
import spring.beautiq.domain.product.dto.request.ProductRequest;
import spring.beautiq.domain.product.dto.common.ProductReqRes;
import spring.beautiq.domain.product.service.ProductService;
import spring.beautiq.global.security.guard.MemberGuard;

import java.util.UUID;

import spring.beautiq.domain.product.dto.common.OrderOption;

@MemberGuard
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/skin-analyses/{analysisId}/recommend-products")
    public ResponseEntity<ProductAIResponse> productsRecommend(
            @PathVariable("analysisId") UUID analysisId,
            @AuthenticationPrincipal OAuth2User principal,
            @Valid @RequestBody ProductRequest request
    ) {

        UUID userId = principal.getAttribute("userId");

        return ResponseEntity.ok(productService.productsRecommend(userId, analysisId, request));
    }

    @PostMapping("/skin-analyses/{analysisId}/recommend-products/wishlists")
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable String analysisId,
    public ResponseEntity<ProductReqRes> addWishlist(
            @Valid @RequestBody ProductReqRes request
    ) {

        UUID userId = principal.getAttribute("userId");

        ProductReqRes created = productService.addWishlist(userUuid, request);

        return ResponseEntity.ok(created);
    }

    @GetMapping("/users/me/wishlist/products")
            @AuthenticationPrincipal OAuth2User principal,
    public ResponseEntity<Page<Product>> getAllWishProduct(
            @RequestParam(name = "order", defaultValue = "newest") String order,
            @RequestParam(name = "page", defaultValue = "1") Integer page
    ) {

        UUID userId = principal.getAttribute("userId");

        OrderOption orderOption = OrderOption.fromParam(order);

        int pageIndex = (page == null || page < 1) ? 0 : page - 1; // 1페이지부터 입력, 0-based로 변환

        return ResponseEntity.ok(productService.getAllWishProduct(userId, orderOption, pageIndex, 10));
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
