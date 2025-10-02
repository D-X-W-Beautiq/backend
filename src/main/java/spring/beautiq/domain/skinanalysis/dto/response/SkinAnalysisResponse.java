package spring.beautiq.domain.skinanalysis.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.skinanalysis.dto.common.SkinAnalysisScores;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysisEntity;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "피부 분석 응답",
    example = """
        {
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
          "skinAnalysis": {
            "dryness": 75,
            "pigmentation": 45,
            "pore": 60,
            "sagging": 30,
            "wrinkle": 40,
            "pigmentationReg": 50,
            "moistureReg": 65,
            "elasticityReg": 78,
            "wrinkleReg": 35,
            "poreReg": 55
          },
          "feedback": "전반적으로 건조한 피부 타입으로 보습 관리가 필요합니다.",
          "averageScore": 72.5,
          "createdAt": "2025-01-15T10:30:00Z"
        }
        """
)
public class SkinAnalysisResponse {

    @NotNull
    @Schema(
        description = "피부 분석 고유 식별자",
        example = "550e8400-e29b-41d4-a716-446655440000",
        type = "string",
        format = "uuid",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String id;

    @NotNull
    @Schema(
        description = "분석 대상 사용자의 고유 식별자",
        example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
        type = "string",
        format = "uuid",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String userId;

    @NotNull
    @Schema(
        description = "피부 분석 세부 점수 결과 (모든 점수는 0-100 범위)",
        implementation = SkinAnalysisScores.class,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private SkinAnalysisScores skinAnalysis;

    @NotNull
    @Schema(
        description = "AI가 생성한 개인화된 피부 분석 피드백 및 관리 조언",
        example = "전반적으로 건조한 피부 타입으로 보습 관리가 필요합니다. 특히 T존 부위의 모공 관리와 함께 수분 공급에 집중하시기 바랍니다.",
        type = "string",
        minLength = 10,
        maxLength = 1000,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String feedback;

    @NotNull
    @Schema(
        description = "전체 피부 상태 평균 점수 (0.0-100.0)",
        example = "72.5",
        type = "number",
        format = "float",
        minimum = "0.0",
        maximum = "100.0",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Float averageScore;

    @NotNull
    @Schema(
        description = "피부 분석이 수행된 일시 (ISO 8601 형식)",
        example = "2025-01-15T10:30:00Z",
        type = "string",
        format = "date-time",
        pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String createdAt;

    public static SkinAnalysisResponse from(SkinAnalysisEntity e) {
        return SkinAnalysisResponse.builder()
                .id(e.getId().toString())
                .userId(e.getUser().getId().toString())
                .skinAnalysis(SkinAnalysisScores.builder()
                        .dryness(e.getDryness())
                        .pigmentation(e.getPigmentation())
                        .pore(e.getPore())
                        .sagging(e.getSagging())
                        .wrinkle(e.getWrinkle())
                        .pigmentationReg(e.getPigmentationReg())
                        .moistureReg(e.getMoistureReg())
                        .elasticityReg(e.getElasticityReg())
                        .wrinkleReg(e.getWrinkleReg())
                        .poreReg(e.getPoreReg())
                        .build())
                .feedback(e.getFeedback())
                .averageScore(e.getAverageScore())
                .createdAt(e.getCreatedAt()
                        .atOffset(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .build();
    }
}