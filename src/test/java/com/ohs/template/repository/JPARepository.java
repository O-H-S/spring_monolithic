package com.ohs.template.repository;


import com.ohs.monolithic.user.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface JPARepository extends JpaRepository<Account, Long> {
    // 추가적인 쿼리 메서드를 여기에 선언할 수 있습니다.
}

interface JPARepository_Crud extends CrudRepository<Account, Long> {
    // 추가적인 쿼리 메서드를 여기에 선언할 수 있습니다.
}

interface JPARepository_PAS extends PagingAndSortingRepository<Account, Long> {
    // 추가적인 쿼리 메서드를 여기에 선언할 수 있습니다.
}

interface JPARepository_2 extends JpaRepository<Account, Long>, QueryByExampleExecutor<Account> {

}

interface JPARepository_3 extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

}