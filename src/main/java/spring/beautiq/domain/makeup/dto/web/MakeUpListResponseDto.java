package spring.beautiq.domain.makeup.dto.web;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MakeUpListResponseDto {
    private List<MakeUpDetailResponseDto> makeUps = new ArrayList<>();
}
