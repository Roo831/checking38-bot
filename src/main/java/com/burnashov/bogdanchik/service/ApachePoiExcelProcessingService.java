package com.burnashov.bogdanchik.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApachePoiExcelProcessingService implements ExcelProcessingService {

    private final KeywordService keywordService;

    @Override
    public void process(InputStream is) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        String keyword = cell.getStringCellValue().trim().toLowerCase();
                        if (!keyword.isBlank()) {
                            keywordService.add(keyword);
                        }
                    }
                }
            }
            log.info("Ключевые слова загружены, всего: {}", keywordService.getKeywords().size());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении Excel-файла", e);
        }
    }
}
