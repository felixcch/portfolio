package com.portfolio.repository;
import com.portfolio.model.instrument.Instrument;
import com.portfolio.model.instrument.InstrumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InstrumentRepository extends JpaRepository<Instrument, String> {
    @Query("SELECT i FROM Instrument i WHERE i.instrumentType = :type")
    List<Instrument> findInstruments(@Param("type") InstrumentType type);
}