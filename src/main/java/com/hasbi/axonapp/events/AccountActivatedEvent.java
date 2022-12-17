package com.hasbi.axonapp.events;

import com.hasbi.axonapp.enums.AccountStatus;
import lombok.Getter;

import java.math.BigDecimal;

public class AccountActivatedEvent extends BaseEvent<String> {
    @Getter private final AccountStatus status;
    public AccountActivatedEvent(String id, AccountStatus status) {
        super(id);
        this.status = status;
    }
}
