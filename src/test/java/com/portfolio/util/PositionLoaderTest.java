package com.portfolio.util;

import com.portfolio.model.PositionLine;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionLoaderTest {

    @Test
    public void loadPositions_shouldLoadPositionsFromCsv() throws Exception {
        // Act
        List<PositionLine> positions = PositionLoader.loadPositions();

        // Assert
        assertEquals(6, positions.size());
        assertEquals("AAPL", positions.get(0).getTicker());
        assertEquals("TSLA", positions.get(1).getTicker());
        assertEquals("AAPL-OCT-2025-300-C", positions.get(2).getTicker());
        assertEquals("AAPL-OCT-2025-110-P", positions.get(3).getTicker());
        assertEquals("TSLA-NOV-2025-400-C", positions.get(4).getTicker());
        assertEquals("TSLA-DEC-2025-400-P" , positions.get(5).getTicker());

        assertEquals(1000, positions.get(0).getShares());
        assertEquals(-500, positions.get(1).getShares());
        assertEquals(-20000, positions.get(2).getShares());
        assertEquals(20000, positions.get(3).getShares());
        assertEquals(10000, positions.get(4).getShares());
        assertEquals(-10000, positions.get(5).getShares());

    }

}
