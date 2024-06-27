package travility_back.travility.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travility_back.travility.dto.BudgetDTO;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long id;

    private boolean isShared; //공유 경비 or 개인 경비

    private String curUnit; //통화 코드

    private double exchangeRate; //환율

    private double amount; //금액

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_book_id")
    private AccountBook accountBook;

    public Budget(BudgetDTO budgetDTO, AccountBook accountBook) {
        this.isShared = budgetDTO.isShared();
        this.curUnit = budgetDTO.getCurUnit();
        this.exchangeRate = budgetDTO.getExchangeRate();
        this.amount = budgetDTO.getAmount();
        this.accountBook = accountBook;
    }

    public BudgetDTO toDTO() {
        return new BudgetDTO(id, isShared, curUnit, exchangeRate, amount);
    }
}