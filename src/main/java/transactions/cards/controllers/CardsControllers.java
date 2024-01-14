package transactions.cards.controllers;

import transactions.cards.dtos.CardsDto;
import transactions.cards.models.BankAccount;
import transactions.cards.services.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/transactions")
public class CardsControllers {

    private final BankAccountService bankAccountService;

    @Autowired
    public CardsControllers(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public ResponseEntity<List<CardsDto>> getAllCards() {
        List<CardsDto> cardsDtoList = bankAccountService.getAllCards();
        return new ResponseEntity<>(cardsDtoList, HttpStatus.OK);
    }

    @GetMapping("/getAllBankAccounts")
    public ResponseEntity<List<BankAccount>> getAllBankAccounts() {
        List<BankAccount> allBankAccounts = bankAccountService.getAllBankAccounts();
        return ResponseEntity.ok(allBankAccounts);
    }

    @PostMapping("/processTransaction")
    public ResponseEntity<Map<String, String>> processTransaction(@RequestBody CardsDto transactionRequest) {
        System.out.println("Transaction request data: " + transactionRequest.toString());

        boolean isTransactionSuccessful = processTransactionRequest(transactionRequest);

        Map<String, String> response = new HashMap<>();
        response.put("status", isTransactionSuccessful ? "approved" : "declined");

        return ResponseEntity.ok(response);
    }

    private boolean processTransactionRequest(CardsDto transactionRequest) {

        Optional<BankAccount> optionalBankAccount = bankAccountService
                .findBankAccount(transactionRequest.getCardNumber(), transactionRequest.getCvc());

        if (optionalBankAccount.isPresent()) {
            BankAccount bankAccount = optionalBankAccount.get();

            if (!bankAccountService.checkCardExpiration(bankAccount)) {
                System.out.println("Card expired.");
                return false;
            }

            if (bankAccountService.checkSufficientFunds(bankAccount, transactionRequest.getBalance())) {
                if (bankAccountService.updateBalance(bankAccount, transactionRequest.getBalance())) {
                    return true;
                } else {
                    System.out.println("Error updating balance.");
                }
            } else {
                System.out.println("Insufficient funds.");
            }
        } else {
            System.out.println("Card not found.");
        }

        return false;
    }
}
