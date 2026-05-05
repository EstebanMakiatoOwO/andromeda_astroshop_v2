package com.andromedaastroshop.crudfullstack.crud_fullstack.currency.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.currency.dto.ExchangeRateResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.currency.dto.UpdateExchangeRateRequest;

public interface ExchangeRateService {

    ExchangeRateResponse getMxnToUsd();

    ExchangeRateResponse updateMxnToUsd(UpdateExchangeRateRequest request);
}