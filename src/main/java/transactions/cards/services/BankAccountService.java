package transactions.cards.services;

import transactions.cards.dtos.CardsDto;
import transactions.cards.models.BankAccount;
import transactions.cards.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public List<CardsDto> getAllCards() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        return bankAccounts.stream().map(this::mapToCardsDto).collect(Collectors.toList());
    }

    private CardsDto mapToCardsDto(BankAccount bankAccount) {
        return new CardsDto(
                bankAccount.getCardNumber(),
                bankAccount.getCvc(),
                bankAccount.getExpirationDate(),
                bankAccount.getBalance()
        );
    }

    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    public Optional<BankAccount> findBankAccount(String cardNumber, Integer cvc, String expirationDate) {
        System.out.println("Searching for card with number: " + cardNumber);
        System.out.println("Searching for card with cvc: " + cvc);
        System.out.println("Searching for card with expiration date: " + expirationDate);

        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findByCardNumberAndCvcAndExpirationDate(cardNumber, cvc, expirationDate);

        if (optionalBankAccount.isPresent()) {
            System.out.println("Card found: " + optionalBankAccount.get());
        } else {
            System.out.println("Card not found.");
        }

        return optionalBankAccount;
    }


    public boolean checkSufficientFunds(BankAccount bankAccount, double requestedAmount) {
        return bankAccount.getBalance() >= requestedAmount;
    }

    public boolean updateBalance(BankAccount bankAccount, double amount) {
        try {
            double newBalance = bankAccount.getBalance() - amount;
            bankAccount.setBalance(newBalance);
            bankAccountRepository.save(bankAccount);
            return true;
        } catch (Exception e) {
            System.out.println("Error updating balance: " + e.getMessage());
            return false;
        }
    }

}