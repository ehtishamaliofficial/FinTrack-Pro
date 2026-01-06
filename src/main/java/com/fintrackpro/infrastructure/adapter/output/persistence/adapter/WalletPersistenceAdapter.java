package com.fintrackpro.infrastructure.adapter.output.persistence.adapter;

import com.fintrackpro.domain.model.Wallet;
import com.fintrackpro.domain.port.output.WalletRepositoryPort;
import com.fintrackpro.domain.valueobject.WalletType;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.WalletEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.repository.JpaWalletRepository;
import com.fintrackpro.infrastructure.helper.EntityReferenceHelper;
import com.fintrackpro.infrastructure.mapper.WalletPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WalletPersistenceAdapter implements WalletRepositoryPort {

    private final JpaWalletRepository jpaWalletRepository;
    private final EntityReferenceHelper entityReferenceHelper;
    private final WalletPersistenceMapper mapper;

    @Override
    public Wallet save(Wallet wallet) {
        return mapper.toDomain(jpaWalletRepository.save(mapper.toEntity(wallet)));
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return jpaWalletRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Wallet> findByUserId(Long userId) {
        return jpaWalletRepository.findByUser(entityReferenceHelper.getUserReference(userId)).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Wallet> findDefaultByUserId(Long userId) {
        return jpaWalletRepository.findByUserAndDefaultWalletTrue(UserEntity.builder().id(userId).build()).map(mapper::toDomain);
    }

    @Override
    public List<Wallet> findByUserIdAndType(Long userId, WalletType type) {
        return jpaWalletRepository.findByUserAndWalletType(entityReferenceHelper.getUserReference(userId),type).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Wallet> findAll() {
        return jpaWalletRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
      jpaWalletRepository.deleteById(id);
    }

    @Override
    public boolean existsByUserIdAndName(Long userId, String name) {
        return jpaWalletRepository.existsByUserAndName(entityReferenceHelper.getUserReference(userId), name);
    }

    @Override
    public Optional<Wallet> updateBalance(Long id, BigDecimal balance) {
        return jpaWalletRepository.findById(id).map(entity->{
            entity.setCurrentBalance(balance);
            return mapper.toDomain(jpaWalletRepository.save(entity));
        });
    }

    @Override
    public Optional<Wallet> updateDefaultStatus(Long id, boolean isDefault) {
        return jpaWalletRepository.findById(id).map(entity->{
            entity.setDefaultWallet(isDefault);
            return mapper.toDomain(jpaWalletRepository.save(entity));
        });
    }

    @Override
    public List<Wallet> findActiveByUserId(Long userId) {
        return jpaWalletRepository.findByUserAndActiveTrue(entityReferenceHelper.getUserReference(userId)).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Wallet> findIncludedInTotalByUserId(Long userId) {
        return jpaWalletRepository.findByUserAndActiveTrueAndExcludedFromTotalFalse(
                entityReferenceHelper.getUserReference(userId)
        ).stream().map(mapper::toDomain).toList();
    }

    @Override
    public BigDecimal calculateTotalBalance(Long userId) {
        List<WalletEntity> wallets = jpaWalletRepository
                .findByUserAndActiveTrueAndExcludedFromTotalFalse(
                        entityReferenceHelper.getUserReference(userId)
                );

        return wallets.stream()
                .map(WalletEntity::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Optional<Wallet[]> transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {
        Optional<WalletEntity> fromWalletOpt = jpaWalletRepository.findById(fromWalletId);
        Optional<WalletEntity> toWalletOpt = jpaWalletRepository.findById(toWalletId);

        // If either wallet doesn't exist, return empty
        if (fromWalletOpt.isEmpty() || toWalletOpt.isEmpty()) {
            return Optional.empty();
        }

        WalletEntity fromWallet = fromWalletOpt.get();
        WalletEntity toWallet = toWalletOpt.get();

        // Update balances
        fromWallet.setCurrentBalance(fromWallet.getCurrentBalance().subtract(amount));
        toWallet.setCurrentBalance(toWallet.getCurrentBalance().add(amount));

        // Save both
        WalletEntity updatedFrom = jpaWalletRepository.save(fromWallet);
        WalletEntity updatedTo = jpaWalletRepository.save(toWallet);

        return Optional.of(new Wallet[]{
                mapper.toDomain(updatedFrom),
                mapper.toDomain(updatedTo)
        });
    }
}
