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
    @Schema(description = "처리 상태 - success | failed", example = "success")
    private String status;

    @Schema(description = "처리된 결과 이미지 이름 - 성공 시 존재", example = "temp/7f000001-9a6e-12b7-819a-6e42edf20000/fc32f75...")
    private String imageName;

    @Schema(description = "처리된 결과 이미지 url - 성공 시 존재", example = "https://beautiq-s3.s3.amazon...")
    private String imageUrl;

    @Schema(description = "실패 시 에러 메시지", example = "Invalid edit parameters")
    private String message;
}
