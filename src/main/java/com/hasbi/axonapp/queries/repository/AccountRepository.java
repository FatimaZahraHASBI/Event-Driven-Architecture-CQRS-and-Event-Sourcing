package com.hasbi.axonapp.queries.repository;

import com.hasbi.axonapp.queries.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<BankAccount,String> {
}
