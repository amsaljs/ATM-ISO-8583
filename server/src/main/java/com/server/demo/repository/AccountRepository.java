package com.server.demo.repository;

import com.server.demo.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByAccountnumber(String AccountNumber);
}
