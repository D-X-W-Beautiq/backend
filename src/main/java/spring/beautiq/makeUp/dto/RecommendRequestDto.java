package spring.beautiq.makeUp.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RecommendRequestDto {

    private MultipartFile originalImg;
    private String keywords;
    private Long analysisId;

}
