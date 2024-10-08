package travility_back.travility.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import travility_back.travility.entity.Budget;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    /**
     * 예산 삭제
     */
    void deleteByAccountBookId(Long accountBookId);

    /**
     * 예산 목록 조회
     */
    List<Budget> findByAccountBookId(Long accountBookId);
}