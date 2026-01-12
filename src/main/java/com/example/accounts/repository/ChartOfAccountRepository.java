package com.example.accounts.repository;

import com.example.accounts.entity.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, Long> {

    Optional<ChartOfAccount> findByAccountCode(String accountCode);

    List<ChartOfAccount> findBySection(String section);

    List<ChartOfAccount> findByParentGroup(String parentGroup);

    List<ChartOfAccount> findByIsActive(Boolean isActive);

    List<ChartOfAccount> findByIsOffBalance(Boolean isOffBalance);

    List<ChartOfAccount> findByIsQuantitative(Boolean isQuantitative);

    boolean existsByAccountCode(String accountCode);
}
