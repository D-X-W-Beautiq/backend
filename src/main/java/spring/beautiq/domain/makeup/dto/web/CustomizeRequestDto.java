package spring.beautiq.domain.makeup.dto.web;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "메이크업 커스터마이즈 요청 DTO",
        example = """
                {
                  "edits": [
                    { "region": "lip", "intensity": 60 },
                    { "region": "skin", "intensity": 40 }
                  ]
                }
                """)
public class CustomizeRequestDto {
//    @NotBlank(message = "imageName은 필수입니다")
//    @Schema(description = "시뮬레이션 된 이미지 이름", example = "temp/7f000001-9a6e-12b7-819a-6e42edf20000/fc32f75...", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String imageName;

    @Valid
    @NotNull(message = "edits는 필수입니다")
    @Schema(description = "편집 항목 배열 (필수)")
    private List<EditForWeb> edits;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "편집 항목")
    public static class EditForWeb {
        // 클라이언트가 is_edited(혹은 edited) 값을 보내지 않으면 null -> 서버에서 기본 true로 처리
        private Boolean isEdited; // optional; null=implicit true
        private String region; // "skin" | "eye" | "lip" | "blush"
        private int intensity; // 0~100

        public Boolean getIsEdited() { return isEdited; }
//        public void setEdited(Boolean edited) { this.edited = edited; }
    }
}
