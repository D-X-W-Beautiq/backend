package spring.beautiq.domain.makeup.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메이크업 커스터마이즈 응답 DTO")
public class CustomizeResponseDto {
    @Schema(description = "커스터마이즈 결과 이미지 (Base64)", example = "/9j/4AAQSkZJRgABAQAAAQABAAD...")
    private String imageBase64;
}

