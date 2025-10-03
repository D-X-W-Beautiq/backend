package spring.beautiq.domain.product.controller;

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
public class ProductController {

    private final ProductService productService;

    @PostMapping("/skin-analyses/{analysisId}/recommend-products")
    public ResponseEntity<ProductResponse> getRecommendProducts(
            @PathVariable("analysisId") UUID analysisId,
            @CurrentUserId UUID userId,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(productService.getRecommendProducts(userId, analysisId, request));
    }

    @PostMapping("/products/{productId}/wishlists")
    public ResponseEntity<WishProductResponse> addWishlist(
            @CurrentUserId UUID userId,
            @PathVariable("productId") UUID productId
    ) {

        return ResponseEntity.ok(productService.addWishlist(userId, productId));
    }

    @GetMapping("/users/me/wishlist/products")
    public ResponseEntity<Page<WishProductResponse>> getAllWishProduct(
            @CurrentUserId UUID userId,
            @RequestParam(name = "order", defaultValue = "newest") String order,
            @RequestParam(name = "page", defaultValue = "1") Integer page
    ) {
        WishlistOrderOption orderOption = WishlistOrderOption.fromParam(order);
        int pageIndex = (page == null || page < 1) ? 0 : page - 1; // 1페이지부터 입력, 0-based로 변환
        return ResponseEntity.ok(productService.getAllWishProduct(userId, orderOption, pageIndex, 10));
    }

    @GetMapping("/users/me/wishlist/products/{productId}")
    public ResponseEntity<WishProductResponse> getWishProduct(
            @CurrentUserId UUID userId,
            @PathVariable("productId") UUID productId
    ) {
        return ResponseEntity.ok(productService.getWishProduct(userId, productId));
    }

    @DeleteMapping("/users/me/wishlist/products/{productId}")
    public ResponseEntity<Void> deleteWishlist(
            @CurrentUserId UUID userId,
            @PathVariable("productId") UUID productId
    ) {
        productService.deleteWishlist(userId, productId);
        return ResponseEntity.noContent().build();
    }
}
