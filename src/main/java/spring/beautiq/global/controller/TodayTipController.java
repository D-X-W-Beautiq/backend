package spring.beautiq.global.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.beautiq.global.dto.TodayTipResponse;
import spring.beautiq.global.service.TodayTipService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/today-tip")
public class TodayTipController {
    private final TodayTipService todayTipService;

    @Operation(
        summary = "오늘의 뷰티 팁 조회",
        description = "하루에 하나, 고정적으로 제공되는 오늘의 뷰티 팁을 반환합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TodayTipResponse.class),
                    examples = @ExampleObject(
                        name = "예시",
                        value = "{\"tip\": \"세안 후에는 즉시 보습제를 발라주세요.\"}"
                    )
                )
            )
        }
    )
    @GetMapping
    public TodayTipResponse getTodayTip() {
        return new TodayTipResponse(todayTipService.getTodayTip());
    }
}
