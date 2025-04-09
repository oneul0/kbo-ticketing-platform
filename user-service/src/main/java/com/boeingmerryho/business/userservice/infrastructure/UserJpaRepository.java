package com.boeingmerryho.business.userservice.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;

public interface UserJpaRepository extends JpaRepository<User, Long>, UserRepository {

}
