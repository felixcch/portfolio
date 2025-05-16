package com.portfolio.util;

import com.google.common.io.Resources;
import com.portfolio.model.Position;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PositionLoader {

    public static List<Position> loadPositions() throws Exception {
        System.out.println("loadPositions : " );
        List<Position> positions = new ArrayList<>();
        try{
            List<String> lines = Resources.readLines(
                    Resources.getResource("position.csv"),
                    StandardCharsets.UTF_8
            );
            System.out.println("lines : " + lines);
            for (String line : lines.subList(1, lines.size())) { // Skip header
                String[] parts = line.split(",");
                positions.add(new Position(parts[0], Integer.parseInt(parts[1])));

            }
        }
        catch (Exception e){
            System.out.println("Failed to read position file");
            throw e;
        }
        return positions;
    }

}
