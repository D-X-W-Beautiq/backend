package spring.beautiq.domain.makeup.dto.web;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "메이크업 커스터마이즈 응답 DTO")
public class CustomizeResponseDto {
    @Schema(description = "처리 상태 - success | failed", example = "success")
    private String status;

    @Schema(description = "처리된 결과 이미지 (Base64) - 성공 시 존재", example = "/9j/4AAQSkZJRgABAQAAAQABAAD...")
    private String resultImageBase64;

    @Schema(description = "실패 시 에러 메시지", example = "Invalid edit parameters")
    private String message;
}
