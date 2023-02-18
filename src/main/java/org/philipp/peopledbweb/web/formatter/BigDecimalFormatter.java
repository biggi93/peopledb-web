package org.philipp.peopledbweb.web.formatter;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@Component
public class BigDecimalFormatter implements Formatter<BigDecimal> {

    private NumberFormat currencyInstance;


    @Override
    public BigDecimal parse(String text, Locale locale) throws ParseException {
        return null;
    }

    @Override
    public String print(BigDecimal object, Locale locale) {
        // durch locale wird über den Browser des Users ermittelt, welche Währung gesetzt werden soll!
        currencyInstance = NumberFormat.getCurrencyInstance(locale);
        return currencyInstance.format(object);
    }
}
