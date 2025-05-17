package com.portfolio.event;

import com.portfolio.model.Position;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

public class PositionUpdateEvent {
    private Collection<Position> positions;
    private CountDownLatch latch;

    public PositionUpdateEvent(Collection<Position> positions, CountDownLatch latch) {
        this.positions = positions;
        this.latch = latch;
    }

    public Collection<Position> getPositions() {
        return positions;
    }

    public void setPositions(Collection<Position> positions) {
        this.positions = positions;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
