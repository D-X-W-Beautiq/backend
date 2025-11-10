package spring.beautiq.domain.makeup.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MakeUpSaveRequestDto {

    @NotBlank(message = "Image Name cannot be blank")
    @Schema(description = "저장할 시뮬레이션 이미지 이름", required = true)
    private String imageName;

    @Schema(description = "키워드 배열")
    private String[] keywords;
}
