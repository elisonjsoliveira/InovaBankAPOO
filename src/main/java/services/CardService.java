package services;

import entities.Card;
import interfaces.ICardService;
import repository.CardRepository;
import java.util.List;
import java.util.Optional;

public class CardService implements ICardService<Card> {

    private final CardRepository cardRepository;
    private final AccountService accountService;

    public CardService(CardRepository cardRepository, AccountService accountService) {
        this.cardRepository = cardRepository;
        this.accountService = accountService;
    }

    @Override
    public void create(Card card) {
        if (card == null) {
            System.out.println("Card can't be null.");
            return;
        }
        if (cardRepository.getByCardNumber(card.getCardNumber()).isPresent()) {
            System.out.println("Card with number " + card.getCardNumber() + " already exists.");
            return;
        }
        if (card.getAccount() == null || accountService.getByAccountNumber(card.getAccount().getAccountNumber()).isEmpty()) {
            System.out.println("Associated account not found or is null. Card not created.");
            return;
        }

        try {
            this.cardRepository.create(card);
            System.out.println("Card created successfully.");
        } catch (RuntimeException e) {
            System.out.println("Failed to create card: " + e.getMessage());
        }
    }

    @Override
    public Optional<Card> getByCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            System.out.println("Invalid card number for lookup.");
            return Optional.empty();
        }
        return cardRepository.getByCardNumber(cardNumber);
    }

    @Override
    public List<Card> getAll() {
        return cardRepository.getAll();
    }

    @Override
    public void update(Card card) {
        if (card == null) {
            System.out.println("Card to update can't be null.");
            return;
        }
        Optional<Card> existingCard = cardRepository.getByCardNumber(card.getCardNumber());
        if (existingCard.isEmpty()) {
            System.out.println("Card with number " + card.getCardNumber() + " not found for update.");
            return;
        }
        if (card.getAccount() != null && accountService.getByAccountNumber(card.getAccount().getAccountNumber()).isEmpty()) {
            System.out.println("Associated account not found. Card update cancelled.");
            return;
        }

        try {
            this.cardRepository.update(card);
            System.out.println("Card updated successfully.");
        } catch (RuntimeException e) {
            System.out.println("Failed to update card: " + e.getMessage());
        }
    }

    @Override
    public void delete(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            System.out.println("Invalid card number for deletion.");
            return;
        }
        try {
            cardRepository.delete(cardNumber);
            System.out.println("Attempted to delete card with number: " + cardNumber + ".");
        } catch (RuntimeException e) {
            System.out.println("Failed to delete card: " + e.getMessage());
        }
    }
}