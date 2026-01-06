package com.fintrackpro.infrastructure.adapter.output.persistence.repository;

import com.fintrackpro.domain.valueobject.WalletType;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaWalletRepository extends JpaRepository<WalletEntity, Long> {

//   @Query("select w from WalletEntity w where w.user=:user")
   List<WalletEntity> findByUser(UserEntity user);

   Optional<WalletEntity> findByUserAndDefaultWalletTrue(UserEntity user);

   List<WalletEntity> findByUserAndWalletType(UserEntity user, WalletType type);

   boolean existsByUserAndName(UserEntity user, String name);

   List<WalletEntity> findByUserAndActiveTrue(UserEntity user);

   List<WalletEntity> findByUserAndActiveTrueAndExcludedFromTotalFalse(UserEntity user);
}
