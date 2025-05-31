package com.jgalat.coupon.springboot.repository;

import com.jgalat.coupon.springboot.model.User;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface UserRepository extends JpaRepository<User, Long> {

	@Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
	User findByUsername(String username);

	boolean existsByUsername(String username);

}
