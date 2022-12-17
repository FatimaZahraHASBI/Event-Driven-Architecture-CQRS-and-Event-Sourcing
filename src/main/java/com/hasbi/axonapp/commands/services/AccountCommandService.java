package com.hasbi.axonapp.commands.services;



import com.hasbi.axonapp.commands.dto.CreateAccountRequestDTO;
import com.hasbi.axonapp.commands.dto.CreditAccountRequestDTO;
import com.hasbi.axonapp.commands.dto.DebitAccountRequestDTO;

import java.util.concurrent.CompletableFuture;

public interface AccountCommandService {
    CompletableFuture<String> createAccount(CreateAccountRequestDTO accountRequestDTO);
    CompletableFuture<String> debitAccount(DebitAccountRequestDTO debitAccountRequestDTO);
    CompletableFuture<String> creditAccount(CreditAccountRequestDTO creditAccountRequestDTO);
}
