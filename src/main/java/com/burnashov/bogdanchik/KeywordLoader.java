package com.burnashov.bogdanchik;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeywordLoader {

    private final Set<String> keywordSet;

    public void loadFromExcel(InputStream is) {
        log.info("Получение ключевых слов из документа");
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        String keyword = cell.getStringCellValue().trim().toLowerCase();
                        if (!keyword.isBlank())
                        {
                            keywordSet.add(keyword);
                        }
                    }
                }
            }
            log.info("Всего ключевых слов - {}", keywordSet.size());
        } catch (IOException e) {
            log.error("Ошибка при чтении Excel-файла");
            throw new RuntimeException("Ошибка при чтении Excel-файла", e);
        }
    }

    public boolean contains(String text) {
        return keywordSet.stream().anyMatch(text::contains);
    }

    public Set<String> getAll() {
        return Collections.unmodifiableSet(keywordSet);
    }
}
