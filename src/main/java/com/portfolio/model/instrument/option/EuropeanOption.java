package com.portfolio.model.instrument.option;

import com.portfolio.model.instrument.Instrument;

import javax.persistence.DiscriminatorValue;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("EUROPEAN_OPTION")
public class EuropeanOption extends Instrument {
    @Enumerated(EnumType.STRING)
    private OptionType optionType;
    private Double strikePrice;
    private LocalDate strikeDate;

    public void setUnderlyingTicker(String underlyingTicker) {
        this.underlyingTicker = underlyingTicker;
    }

    public void setStrikeDate(LocalDate strikeDate) {
        this.strikeDate = strikeDate;
    }

    public void setStrikePrice(Double strikePrice) {
        this.strikePrice = strikePrice;
    }

    public void setOptionType(OptionType optionType) {
        this.optionType = optionType;
    }

    private String underlyingTicker;

    public OptionType getOptionType() {
        return optionType;
    }

    public Double getStrikePrice() {
        return strikePrice;
    }

    public LocalDate getStrikeDate() {
        return strikeDate;
    }

    public String getUnderlyingTicker() {
        return underlyingTicker;
    }



}
