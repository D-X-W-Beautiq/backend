package spring.beautiq.domain.skinanalysis.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SkinAnalysisRequestDto {

    private MultipartFile faceImageFile;
}
