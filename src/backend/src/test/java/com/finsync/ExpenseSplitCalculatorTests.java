package com.finsync;

import com.finsync.dto.GroupExpenseDtos.GroupExpenseSplitInput;
import com.finsync.entity.enums.CurrencyCode;
import com.finsync.entity.enums.SplitMode;
import com.finsync.exception.ApiException;
import com.finsync.service.impl.ExpenseSplitCalculator;
import com.finsync.service.impl.MoneyValidation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseSplitCalculatorTests {

    private final ExpenseSplitCalculator calculator = new ExpenseSplitCalculator();

    @Test
    void equalSplitConservesIndivisibleVndAndUsdTotals() {
        UUID m1 = UUID.randomUUID();
        UUID m2 = UUID.randomUUID();
        UUID m3 = UUID.randomUUID();
        var participants = List.of(
                new GroupExpenseSplitInput(m1, null),
                new GroupExpenseSplitInput(m2, null),
                new GroupExpenseSplitInput(m3, null)
        );

        var vnd = calculator.calculate(new BigDecimal("100000.00"), CurrencyCode.VND, SplitMode.EQUAL, participants);
        assertEquals(new BigDecimal("33334.00"), vnd.get(0).amount());
        assertEquals(new BigDecimal("33333.00"), vnd.get(1).amount());
        assertEquals(new BigDecimal("33333.00"), vnd.get(2).amount());
        assertNull(vnd.get(0).inputValue());

        var usd = calculator.calculate(new BigDecimal("10.00"), CurrencyCode.USD, SplitMode.EQUAL, participants);
        assertEquals(new BigDecimal("3.34"), usd.get(0).amount());
        assertEquals(new BigDecimal("3.33"), usd.get(1).amount());
        assertEquals(new BigDecimal("3.33"), usd.get(2).amount());
    }

    @Test
    void percentageSplitUsesLargestRemainderWithOrderTieBreak() {
        UUID m1 = UUID.randomUUID();
        UUID m2 = UUID.randomUUID();
        UUID m3 = UUID.randomUUID();
        var participants = List.of(
                new GroupExpenseSplitInput(m1, new BigDecimal("33.33")),
                new GroupExpenseSplitInput(m2, new BigDecimal("33.33")),
                new GroupExpenseSplitInput(m3, new BigDecimal("33.34"))
        );

        var result = calculator.calculate(new BigDecimal("100000.00"), CurrencyCode.VND, SplitMode.PERCENTAGE, participants);
        BigDecimal sum = result.stream().map(ExpenseSplitCalculator.ComputedSplit::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(new BigDecimal("100000.00"), sum);
        assertEquals(new BigDecimal("33330.00"), result.get(0).amount());
        assertEquals(new BigDecimal("33330.00"), result.get(1).amount());
        assertEquals(new BigDecimal("33340.00"), result.get(2).amount());
    }

    @Test
    void sharesSplitConservesTotalAcrossUnequalWeights() {
        UUID m1 = UUID.randomUUID();
        UUID m2 = UUID.randomUUID();
        UUID m3 = UUID.randomUUID();
        var participants = List.of(
                new GroupExpenseSplitInput(m1, new BigDecimal("1")),
                new GroupExpenseSplitInput(m2, new BigDecimal("2")),
                new GroupExpenseSplitInput(m3, new BigDecimal("3"))
        );

        var result = calculator.calculate(new BigDecimal("100000.00"), CurrencyCode.VND, SplitMode.SHARES, participants);
        BigDecimal sum = result.stream().map(ExpenseSplitCalculator.ComputedSplit::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(new BigDecimal("100000.00"), sum);
        assertEquals(new BigDecimal("16667.00"), result.get(0).amount());
        assertEquals(new BigDecimal("33333.00"), result.get(1).amount());
        assertEquals(new BigDecimal("50000.00"), result.get(2).amount());
    }

    @Test
    void exactSplitAndValidationRejectInvalidInputs() {
        UUID m1 = UUID.randomUUID();
        UUID m2 = UUID.randomUUID();

        var exact = calculator.calculate(new BigDecimal("50000.00"), CurrencyCode.VND, SplitMode.EXACT, List.of(
                new GroupExpenseSplitInput(m1, new BigDecimal("20000")),
                new GroupExpenseSplitInput(m2, new BigDecimal("30000"))
        ));
        assertEquals(new BigDecimal("20000.00"), exact.get(0).amount());
        assertEquals(new BigDecimal("30000.00"), exact.get(1).amount());

        assertThrows(ApiException.class, () -> calculator.calculate(
                new BigDecimal("50000.00"), CurrencyCode.VND, SplitMode.EXACT, List.of(
                        new GroupExpenseSplitInput(m1, new BigDecimal("20000")),
                        new GroupExpenseSplitInput(m2, new BigDecimal("25000"))
                )));
        assertThrows(ApiException.class, () -> MoneyValidation.requirePositiveAmount(
                new BigDecimal("100.50"), CurrencyCode.VND, "amount"));
        assertThrows(ApiException.class, () -> calculator.calculate(
                new BigDecimal("100.00"), CurrencyCode.USD, SplitMode.PERCENTAGE, List.of(
                        new GroupExpenseSplitInput(m1, new BigDecimal("50")),
                        new GroupExpenseSplitInput(m2, new BigDecimal("49"))
                )));
    }
}
