package com.demetrius.vellastra.common.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PageResult}
 */
class PageResultTest {

    @Test
    @DisplayName("of() 应正确计算总页数")
    void of_shouldCalculatePages() {
        List<String> records = List.of("a", "b");
        PageResult<String> result = PageResult.of(records, 25, 1, 10);

        assertEquals(records, result.getRecords());
        assertEquals(25, result.getTotal());
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
        assertEquals(3, result.getPages());
    }

    @Test
    @DisplayName("of() 空数据时 pages 应为 0")
    void of_emptyRecords_shouldReturnZeroPages() {
        PageResult<String> result = PageResult.of(List.of(), 0, 1, 10);
        assertEquals(0, result.getPages());
    }
}