package com.hasbi.axonapp.queries.services;

import com.hasbi.axonapp.enums.OperationType;
import com.hasbi.axonapp.events.AccountActivatedEvent;
import com.hasbi.axonapp.events.AccountCreatedEvent;
import com.hasbi.axonapp.events.AccountCreditedEvent;
import com.hasbi.axonapp.events.AccountDebitedEvent;
import com.hasbi.axonapp.mappers.BankAccountMapper;
import com.hasbi.axonapp.queries.dto.*;
import com.hasbi.axonapp.queries.entities.AccountOperation;
import com.hasbi.axonapp.queries.entities.BankAccount;
import com.hasbi.axonapp.queries.repository.AccountOperationRepository;
import com.hasbi.axonapp.queries.repository.AccountRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountQueryService {
    private final AccountRepository accountRepository;
    private final AccountOperationRepository accountOperationRepository;
    private final BankAccountMapper bankAccountMapper;
    private final QueryUpdateEmitter queryUpdateEmitter;

    public AccountQueryService(AccountRepository accountRepository, AccountOperationRepository accountOperationRepository, BankAccountMapper bankAccountMapper, QueryUpdateEmitter queryUpdateEmitter) {
        this.accountRepository = accountRepository;
        this.accountOperationRepository = accountOperationRepository;
        this.bankAccountMapper = bankAccountMapper;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @EventHandler
    @Transactional
    public void on(AccountCreatedEvent accountCreatedEvent){
        BankAccount bankAccount=new BankAccount();
        bankAccount.setId(accountCreatedEvent.getId());
        bankAccount.setBalance(accountCreatedEvent.getBalance());
        bankAccount.setStatus(accountCreatedEvent.getStatus());
        accountRepository.save(bankAccount);
    }
    @EventHandler
    @Transactional
    public void on(AccountActivatedEvent accountActivatedEvent){
        BankAccount bankAccount=accountRepository.findById(accountActivatedEvent.getId()).get();
        bankAccount.setStatus(accountActivatedEvent.getStatus());
        accountRepository.save(bankAccount);
    }
    @EventHandler
    @Transactional
    public void on(AccountDebitedEvent accountDebitedEvent){
        BankAccount bankAccount=accountRepository.findById(accountDebitedEvent.getId()).get();
        bankAccount.setBalance(bankAccount.getBalance().subtract(accountDebitedEvent.getAmount()));
        BankAccount savedAccount =accountRepository.save(bankAccount);
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setOperationDate(new Date());
        accountOperation.setAmount(accountDebitedEvent.getAmount());
        accountOperation.setBankAccount(savedAccount);
        accountOperation.setType(OperationType.DEBIT);
        accountOperationRepository.save(accountOperation);
        queryUpdateEmitter.emit(m->(
                        (GetAccountQueryDTO)m.getPayload()).getId().equals(accountDebitedEvent.getId()),
                bankAccountMapper.bankAccountToBankAccountDTO(bankAccount)
        );
    }
    @EventHandler
    @Transactional
    public void on(AccountCreditedEvent accountCreditedEvent){
        BankAccount bankAccount=accountRepository.findById(accountCreditedEvent.getId()).get();
        bankAccount.setBalance(bankAccount.getBalance().add(accountCreditedEvent.getAmount()));
        BankAccount savedAccount = accountRepository.save(bankAccount);
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setOperationDate(new Date());
        accountOperation.setAmount(accountCreditedEvent.getAmount());
        accountOperation.setBankAccount(savedAccount);
        accountOperation.setType(OperationType.CREDIT);
        accountOperationRepository.save(accountOperation);
        queryUpdateEmitter.emit(m->(
                (GetAccountQueryDTO)m.getPayload()).getId().equals(accountCreditedEvent.getId()),
                bankAccountMapper.bankAccountToBankAccountDTO(bankAccount)
        );
    }

    @QueryHandler
    public BankAccountResponseDTO on(GetAccountQueryDTO accountQuery) {
        BankAccount bankAccount = accountRepository.findById(accountQuery.getId()).get();
        return bankAccountMapper.bankAccountToBankAccountDTO(bankAccount);
    }
    @QueryHandler
    public List<BankAccountResponseDTO> on(GetAllAccountsRequestDTO accountsRequest) {
        List<BankAccount> bankAccountList = accountRepository.findAll();
        return bankAccountList.stream().map((acc->bankAccountMapper.bankAccountToBankAccountDTO(acc))).collect(Collectors.toList());
    }
    @QueryHandler
    public List<AccountOperationResponseDTO> on(GetAccountOperationsQueryDTO getAccountOperationsQueryDTO) {
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(getAccountOperationsQueryDTO.getAccountId());
        return accountOperations.stream().map(op->bankAccountMapper.accountOperationToAccountOperationDTO(op)).collect(Collectors.toList());
    }
}
