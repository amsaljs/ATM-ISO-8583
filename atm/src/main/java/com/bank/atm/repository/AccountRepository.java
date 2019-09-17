package com.bank.atm.repository;

import com.bank.atm.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByAccnumber(String AccountNumber);
    Account findByVirtualaccountdana(String virtualAccount);
}
