package spring.beautiq.domain.product.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import spring.beautiq.domain.product.entity.ProductEntity;
import spring.beautiq.domain.product.repository.ProductRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 앱 시작 시 ProductEntity 테이블이 비어있으면 올리브영 엑셀 파일에서 한 번만 데이터를 로드
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductInitialLoader implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final ResourceLoader resourceLoader;

    @Value("${app.product.init.enabled:true}")
    private boolean enabled;

    @Value("${app.product.init.file:classpath:oliveyoung_data_sets.xlsx}")
    private String filePath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("[ProductInitialLoader] === 시작 ===");

        if (!enabled) {
            log.info("[ProductInitialLoader] 비활성화됨 (enabled=false)");
            return;
        }

        long existingCount = productRepository.count();
        log.info("[ProductInitialLoader] 현재 ProductEntity 테이블 데이터 수: {}", existingCount);

        if (existingCount > 0) {
            log.info("[ProductInitialLoader] ProductEntity 테이블에 이미 {}개의 데이터가 존재하여 초기화를 스킵합니다", existingCount);
            return;
        }

        log.info("[ProductInitialLoader] 데이터 로드 시작 - 파일: {}", filePath);

        Resource resource = resourceLoader.getResource(filePath);
        if (!resource.exists()) {
            log.error("[ProductInitialLoader] 파일을 찾을 수 없습니다: {}", filePath);
            return;
        }

        log.info("[ProductInitialLoader] 파일 발견: {} (크기: {} bytes)",
                resource.getFilename(), resource.contentLength());

        try {
            List<ProductEntity> products = loadFromExcel(resource);
            if (products.isEmpty()) {
                log.warn("[ProductInitialLoader] 로드된 상품이 없어 저장을 건너뜁니다.");
                return;
            }
            productRepository.saveAll(products); // 한 번에 저장
            log.info("[ProductInitialLoader] === 완료 === {}개의 상품 데이터를 한 번에 저장했습니다", products.size());
        } catch (Exception e) {
            log.error("[ProductInitialLoader] 데이터 로드/저장 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    private List<ProductEntity> loadFromExcel(Resource resource) throws Exception {
        List<ProductEntity> products = new ArrayList<>();

        try (InputStream is = resource.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            log.info("[ProductInitialLoader] Excel 시트 발견: {} (총 {}행)", sheet.getSheetName(), sheet.getLastRowNum() + 1);

            boolean isHeader = true;
            int rowNumber = 0;

            for (Row row : sheet) {
                rowNumber++;

                if (isHeader) { // 헤더 1행 스킵
                    isHeader = false;
                    log.info("[ProductInitialLoader] Excel 헤더 스킵 (행 1)");
                    continue;
                }

                if (row.getPhysicalNumberOfCells() < 3) { // 최소 컬럼 수 체크
                    continue;
                }

                ProductEntity product = createProductFromRow(row, rowNumber);
                if (product == null) continue;

                // 중복 체크 제거 - 모든 데이터를 DB에 저장
                products.add(product);
            }
        }

        return products;
    }

    private ProductEntity createProductFromRow(Row row, int rowNumber) {
        try {
            String category = getCellValue(row, 0);
            String productName = getCellValue(row, 5);
            String productUrl = getCellValue(row, 15);

            if (rowNumber <= 10) {
                log.info("[ProductInitialLoader] 행 {} 파싱: category='{}', productName='{}', productUrl='{}'",
                        rowNumber, category, productName, productUrl);
            }

            if (isBlank(category) || isBlank(productName) || isBlank(productUrl)) {
                if (rowNumber <= 10) {
                    log.warn("[ProductInitialLoader] 필수 필드 누락으로 스킵 (행 {}): category={}, productName={}, productUrl={}",
                            rowNumber, category, productName, productUrl);
                }
                return null;
            }

            return ProductEntity.builder()
                    .category(category)
                    .overallRank(parseInteger(getCellValue(row, 1)))
                    .pageNumber(parseInteger(getCellValue(row, 2)))
                    .pageRank(parseInteger(getCellValue(row, 3)))
                    .brand(getCellValue(row, 4))
                    .productName(productName)
                    .listPrice(parseInteger(getCellValue(row, 6)))
                    .salePrice(parseInteger(getCellValue(row, 7)))
                    .reviewScore(parseDouble(getCellValue(row, 8)))
                    .reviewCount(parseInteger(getCellValue(row, 9)))
                    .ingredients(getCellValue(row, 10))
                    .description(getCellValue(row, 11))
                    .tags(getCellValue(row, 12))
                    .bestOrNew(getCellValue(row, 13))
                    .imageUrl(getCellValue(row, 14))
                    .productUrl(productUrl)
                    .build();

        } catch (Exception e) {
            log.error("[ProductInitialLoader] 행 {} 파싱 실패: {}", rowNumber, e.getMessage());
            return null;
        }
    }

    private String getCellValue(Row row, int columnIndex) {
        try {
            if (row.getCell(columnIndex) == null) {
                return null;
            }
            org.apache.poi.ss.usermodel.Cell cell = row.getCell(columnIndex);
            String value = null;
            switch (cell.getCellType()) {
                case STRING:
                    value = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        value = cell.getDateCellValue().toString();
                    } else {
                        double numValue = cell.getNumericCellValue();
                        if (numValue == Math.floor(numValue)) {
                            value = String.valueOf((long) numValue);
                        } else {
                            value = String.valueOf(numValue);
                        }
                    }
                    break;
                case BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
                case FORMULA:
                    try {
                        value = cell.getStringCellValue();
                    } catch (Exception e) {
                        try {
                            double numValue = cell.getNumericCellValue();
                            if (numValue == Math.floor(numValue)) {
                                value = String.valueOf((long) numValue);
                            } else {
                                value = String.valueOf(numValue);
                            }
                        } catch (Exception e2) {
                            value = null;
                        }
                    }
                    break;
                case BLANK:
                case _NONE:
                default:
                    value = null;
                    break;
            }
            return (value == null || value.trim().isEmpty()) ? null : value.trim();
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        if (isBlank(value)) return null;
        try {
            String cleanValue = value.replaceAll("[^0-9]", "");
            return cleanValue.isEmpty() ? null : Integer.valueOf(cleanValue);
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        if (isBlank(value)) return null;
        try {
            String cleanValue = value.replaceAll("[^0-9.]", "");
            if (cleanValue.isEmpty()) return null;
            int firstDotIndex = cleanValue.indexOf('.');
            if (firstDotIndex != -1) {
                String beforeDot = cleanValue.substring(0, firstDotIndex);
                String afterDot = cleanValue.substring(firstDotIndex + 1).replaceAll("\\.", "");
                cleanValue = beforeDot + "." + afterDot;
            }

            return Double.valueOf(cleanValue);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
