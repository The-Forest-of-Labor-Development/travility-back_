package travility_back.travility.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AccountBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_book_id")
    private Long id;

    @Column(nullable = false)
    private Date startDate; // 여행 시작일자

    @Column(nullable = false)
    private Date endDate; // 여행 종료일자

    @Column(nullable = false)
    private String countryName;

    @Column(nullable = false)
    private String countryFlag;

    @Column(nullable = false)
    private int numberOfPeople; //인원

    @Column(nullable = false)
    private String title; // 제목

    private String imgName;

    @OneToMany(mappedBy = "accountBook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Budget> budgets = new ArrayList<>();

    @OneToMany(mappedBy = "accountBook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}