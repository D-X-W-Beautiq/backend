package spring.beautiq.domain.makeup.dto.web;

import lombok.Data;

@Data
public class MakeUpSaveRequestDto {
    private String imageName;
    private String[] keywords;
}
