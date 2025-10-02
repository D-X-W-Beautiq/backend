package spring.beautiq.domain.skinanalysis.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysisEntity;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@ArraySchema(
    schema = @Schema(implementation = DailySkinDatesResponse.DailySkinDateItem.class),
    minItems = 0,
    maxItems = 1000
)
@Schema(
    description = "일별 피부 분석 날짜 목록 배열 (오름차순: 생성일 기준)",
    example = "[{\"id\":\"550e8400-e29b-41d4-a716-446655440000\",\"date\":\"2024-08-18T09:12\"},{\"id\":\"a1b2c3d4-e5f6-7890-abcd-ef1234567890\",\"date\":\"2024-08-19T14:30\"}]",
    type = "array"
)
public class DailySkinDatesResponse {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "피부 분석 날짜 항목")
    public static class DailySkinDateItem {

        @NotNull
        @Schema(
            description = "피부 분석 고유 식별자",
            example = "550e8400-e29b-41d4-a716-446655440000",
            type = "string",
            format = "uuid",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String id; // 필드명 변경 (skinAnalysisId -> id)

        @NotNull
        @Schema(
            description = "피부 분석 수행 날짜 및 시간 (yyyy-MM-dd'T'HH:mm / 분 단위까지 저장)",
            example = "2024-08-18T09:12",
            type = "string",
            format = "date-time",
            pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String date;
    }

    public static List<DailySkinDateItem> from(List<SkinAnalysisEntity> skinAnalyses) {
        return (skinAnalyses == null ? List.<SkinAnalysisEntity>of() : skinAnalyses).stream()
                .sorted(Comparator.comparing(SkinAnalysisEntity::getCreatedAt))
                .map(sa -> DailySkinDateItem.builder()
                        .id(sa.getId().toString())
                        .date(sa.getCreatedAt().format(FMT))
                        .build())
                .toList();
    }
}
