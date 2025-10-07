package spring.beautiq.domain.makeup.dto.web;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MakeUpDetailResponseDto {
    private UUID makeUpId;
    private String imageName;
    private String imageUrl;
    private List<String> keywords;
    private String createdAt;
}