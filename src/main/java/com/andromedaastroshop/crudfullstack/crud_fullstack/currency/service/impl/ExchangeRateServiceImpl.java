package com.andromedaastroshop.crudfullstack.crud_fullstack.currency.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.currency.dto.ExchangeRateResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.currency.dto.UpdateExchangeRateRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.currency.model.ExchangeRate;
import com.andromedaastroshop.crudfullstack.crud_fullstack.currency.repository.ExchangeRateRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.currency.service.ExchangeRateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private static final String FROM = "MXN";
    private static final String TO = "USD";
    private static final BigDecimal DEFAULT_RATE = new BigDecimal("0.050000");

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateServiceImpl(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public ExchangeRateResponse getMxnToUsd() {
        ExchangeRate rate = exchangeRateRepository.findByFromCurrencyAndToCurrency(FROM, TO)
                .orElseGet(this::createDefault);
        return mapToResponse(rate);
    }

    @Override
    @Transactional
    public ExchangeRateResponse updateMxnToUsd(UpdateExchangeRateRequest request) {
        ExchangeRate rate = exchangeRateRepository.findByFromCurrencyAndToCurrency(FROM, TO)
                .orElseGet(this::createDefault);
        rate.setRate(request.rate());
        return mapToResponse(exchangeRateRepository.save(rate));
    }

    private ExchangeRate createDefault() {
        ExchangeRate rate = new ExchangeRate();
        rate.setFromCurrency(FROM);
        rate.setToCurrency(TO);
        rate.setRate(DEFAULT_RATE);
        return exchangeRateRepository.save(rate);
    }

    private ExchangeRateResponse mapToResponse(ExchangeRate rate) {
        return new ExchangeRateResponse(
                rate.getId(),
                rate.getFromCurrency(),
                rate.getToCurrency(),
                rate.getRate(),
                rate.getUpdatedAt()
        );
    }
}