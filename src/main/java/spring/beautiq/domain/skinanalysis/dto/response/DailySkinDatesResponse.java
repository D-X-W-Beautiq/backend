package spring.beautiq.domain.skinanalysis.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysis;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailySkinDatesResponse {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @NotNull
    private List<String> skinAnalysisDates;

    public static DailySkinDatesResponse from(List<SkinAnalysis> skinAnalyses) {
        return DailySkinDatesResponse.builder()
                .skinAnalysisDates((skinAnalyses == null ? List.<SkinAnalysis>of() : skinAnalyses).stream()
                        .sorted(Comparator.comparing(SkinAnalysis::getCreatedAt))
                        .map(sa -> sa.getCreatedAt().format(FMT))
                        .toList())
                .build();
    }
}
