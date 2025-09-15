package org.example.startapi.domain.repository;

import org.example.startapi.domain.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAdminIdAndAdminPassword(String adminId, String adminPassword);

}
