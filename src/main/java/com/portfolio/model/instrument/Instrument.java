package com.portfolio.model.instrument;

import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "instrument_type", discriminatorType = DiscriminatorType.STRING)
public class Instrument {

    @Id
    private String ticker;
    @Enumerated(EnumType.STRING)
    @Column(name = "instrument_type", insertable = false, updatable = false)
    private InstrumentType instrumentType;
    public String getTicker() { return ticker; }
    public InstrumentType getInstrumentType() { return instrumentType; }
}

