package spring.beautiq.domain.makeup.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecommendResponseDto {

    private List<String> recommendations; // s3 임시 접근 url 리스트 전송

}
