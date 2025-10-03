package spring.beautiq.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import spring.beautiq.global.security.guard.MemberGuard;

@MemberGuard
@RestController
@RequiredArgsConstructor
public class ProductController {

//    private final ProductService productService;
//
//    @PostMapping("/skin-analyses/{analysisId}/recommend-products")
//    public ResponseEntity<ProductAIResponse> productsRecommend(
//            @PathVariable("analysisId") UUID analysisId,
//            @CurrentUserId UUID userId,
//            @Valid @RequestBody ProductRequest request
//    ) {
//        return ResponseEntity.ok(productService.productsRecommend(userId, analysisId, request));
//    }
//
//    @PostMapping("/users/me/wishlist/products")
//    public ResponseEntity<ProductReqRes> addWishlist(
//            @CurrentUserId UUID userId,
//            @Valid @RequestBody ProductReqRes request
//    ) {
//        ProductReqRes created = productService.addWishlist(userId, request);
//        return ResponseEntity.ok(created);
//    }
//
//    @GetMapping("/users/me/wishlist/products")
//    public ResponseEntity<Page<Product>> getAllWishProduct(
//            @CurrentUserId UUID userId,
//            @RequestParam(name = "order", defaultValue = "newest") String order,
//            @RequestParam(name = "page", defaultValue = "1") Integer page
//    ) {
//        OrderOption orderOption = OrderOption.fromParam(order);
//        int pageIndex = (page == null || page < 1) ? 0 : page - 1; // 1페이지부터 입력, 0-based로 변환
//        return ResponseEntity.ok(productService.getAllWishProduct(userId, orderOption, pageIndex, 10));
//    }
//
//    @GetMapping("/users/me/wishlist/products/{productId}")
//    public ResponseEntity<ProductReqRes> getWishProduct(
//            @CurrentUserId UUID userId,
//            @PathVariable("productId") UUID productId
//    ) {
//        return ResponseEntity.ok(productService.getWishProduct(userId, productId));
//    }
//
//    @DeleteMapping("/users/me/wishlist/products/{productId}")
//    public ResponseEntity<Void> deleteWishlist(
//            @CurrentUserId UUID userId,
//            @PathVariable("productId") UUID productId
//    ) {
//        productService.deleteWishlist(userId, productId);
//        return ResponseEntity.noContent().build();
//    }
}
