package transactions.cards.repositories;

import transactions.cards.models.BankAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends MongoRepository<BankAccount, String> {
    Optional<BankAccount> findByCardNumberAndCvc(String cardNumber, Integer cvc);
    List<BankAccount> findAll();

    Optional<BankAccount> findByCardNumberAndCvcAndExpirationDate(String cardNumber, Integer cvc, String expirationDate);
}
