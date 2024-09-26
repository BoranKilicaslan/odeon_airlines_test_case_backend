package com.odeon.odeon_airlines.repository;

import com.odeon.odeon_airlines.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

 public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
