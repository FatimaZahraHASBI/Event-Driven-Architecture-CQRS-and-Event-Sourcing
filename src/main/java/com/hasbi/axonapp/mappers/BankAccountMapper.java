package com.hasbi.axonapp.mappers;

import com.hasbi.axonapp.queries.dto.AccountOperationResponseDTO;
import com.hasbi.axonapp.queries.dto.BankAccountResponseDTO;
import com.hasbi.axonapp.queries.entities.AccountOperation;
import com.hasbi.axonapp.queries.entities.BankAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    BankAccountResponseDTO bankAccountToBankAccountDTO(BankAccount bankAccount);
    AccountOperationResponseDTO accountOperationToAccountOperationDTO(AccountOperation accountOperation);
}
