package spring.beautiq.domain.product.wishlist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "위시리스트 토글 응답 DTO",
        example = """
                {
                  "productId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                  "isWish": true,
                  "message": "위시리스트에 추가되었습니다."
                }
                """
)
public class WishlistToggleResponse {

    @NotNull
    @Schema(
            description = "제품 ID",
            example = "7c9e6679-7425-40de-944b-e07fc1f90ae7",
            type = "string",
            format = "uuid",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String productId;

    @NotNull
    @Schema(
            description = "현재 위시리스트 포함 여부 (true: 추가됨, false: 제거됨)",
            example = "true",
            type = "boolean",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean isWish;

    @NotNull
    @Schema(
            description = "작업 결과 메시지",
            example = "위시리스트에 추가되었습니다.",
            type = "string",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String message;

    public static WishlistToggleResponse of(UUID productId, boolean isWish) {
        String message = isWish
                ? "위시리스트에 추가되었습니다."
                : "위시리스트에서 제거되었습니다.";

        return WishlistToggleResponse.builder()
                .productId(productId.toString())
                .isWish(isWish)
                .message(message)
                .build();
    }
}

