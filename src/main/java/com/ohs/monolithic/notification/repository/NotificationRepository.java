package com.ohs.monolithic.notification.repository;

import com.ohs.monolithic.notification.domain.NotificationBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface NotificationRepository<T extends NotificationBase, ID extends Serializable> extends JpaRepository<T, ID> {


}