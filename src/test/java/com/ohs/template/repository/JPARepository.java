package com.ohs.template.repository;

import com.ohs.monolithic.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface JPARepository extends JpaRepository<User, Long> {
    // 추가적인 쿼리 메서드를 여기에 선언할 수 있습니다.
}

interface JPARepository_Crud extends CrudRepository<User, Long> {
    // 추가적인 쿼리 메서드를 여기에 선언할 수 있습니다.
}

interface JPARepository_PAS extends PagingAndSortingRepository<User, Long> {
    // 추가적인 쿼리 메서드를 여기에 선언할 수 있습니다.
}

interface JPARepository_2 extends JpaRepository<User, Long>, QueryByExampleExecutor<User> {

}

interface JPARepository_3 extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

}