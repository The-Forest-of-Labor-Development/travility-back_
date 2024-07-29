package travility_back.travility.controller.statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import travility_back.travility.dto.auth.CustomUserDetails;
import travility_back.travility.dto.statistics.*;
import travility_back.travility.entity.Member;
import travility_back.travility.entity.enums.Category;
import travility_back.travility.repository.MemberRepository;
import travility_back.travility.service.statistic.StatisticService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

//    /**
//     * 마이 리포트
//     */
//    @GetMapping("/detail")
//    public ResponseEntity<MyReportExpenseStatisticsDTO> getStatistics() {
//        MyReportExpenseStatisticsDTO statisticsDto = statisticService.getStatistics();
//        return ResponseEntity.ok(statisticsDto);
//    }
//
//    @GetMapping("/userinfo")
//    public ResponseEntity<Member> getUserInfo() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        Member member = statisticService.getMemberByUsername(username);
//        return ResponseEntity.ok(member);
//    }
//
//
//
//    /**
//     * 지출 통계
//     */
//
//    // [Select 메뉴] 카테고리별 바 차트
//    @GetMapping("/statistics/category")
//    public ResponseEntity<List<DateCategoryAmountDTO>> getStatisticsByDate(@RequestParam Long accountBookId) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Long memberId = statisticService.getMemberIdByUsername(username);
//        List<DateCategoryAmountDTO> statistics = statisticService.getStatisticsByDate(accountBookId, memberId);
//        return ResponseEntity.ok(statistics);
//    }
//
//    // [Select 메뉴] 결제방법별 바 차트
//    @GetMapping("/statistics/paymentMethod")
//    public ResponseEntity<List<PaymentMethodAmountDTO>> getPaymentMethodStatistics(
//            @RequestParam Long accountBookId,
//            @RequestParam String date) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Long memberId = statisticService.getMemberIdByUsername(username);
//
//        LocalDateTime startOfDay = LocalDateTime.parse(date + "T00:00:00");
//        LocalDateTime endOfDay = LocalDateTime.parse(date + "T23:59:59");
//
//        List<PaymentMethodAmountDTO> statistics = statisticService.getPaymentMethodStatistics(accountBookId, memberId, startOfDay, endOfDay);
//        return ResponseEntity.ok(statistics);
//    }
//
//    // 카테고리별 전체 지출
//    @GetMapping("/statistics/totalcategory")
//    public ResponseEntity<List<DateCategoryAmountDTO>> getTotalAmountByCategoryForAll(@RequestParam Long accountBookId) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Long memberId = statisticService.getMemberIdByUsername(username);
//        List<DateCategoryAmountDTO> statistics = statisticService.getTotalAmountByCategoryForAll(accountBookId, memberId);
//        return ResponseEntity.ok(statistics);
//    }
//
//    //==예산 - 지출==//
//
//    /**
//     * 총 예산 가져오기
//     * @param accountBookId 요청할 가계부
//     * @return 해당 가계부 총 예산
//     */
//    @GetMapping("/statistics/totalbudget")
//    public ResponseEntity<Double> getTotalBudgetByAccountBookId(@RequestParam Long accountBookId) {
//        Double totalBudget = statisticService.getTotalBudgetByAccountBookId(accountBookId);
//        return ResponseEntity.ok(totalBudget);
//    }
//
//    /**
//     * 총 지출 가져오기
//     * @param accountBookId 요청할 가계부
//     * @return 해당 가계부 총 지출
//     */
//    @GetMapping("/statistics/totalexpense")
//    public ResponseEntity<Double> getTotalExpenseByAccountBookId(@RequestParam Long accountBookId) {
//        Double totalExpense = statisticService.getTotalExpenseByAccountBookId(accountBookId);
//        return ResponseEntity.ok(totalExpense);
//    }
//
//    /**
//     * 남은 예산 가져오기
//     * @param accountBookId 요청할 가계부
//     * @return 해당 가계부의 남은 예산
//     */
//    @GetMapping("/statistics/remaining-budget")
//    public ResponseEntity<Double> getRemainingBudget(@RequestParam Long accountBookId) {
//        Double remainingBudget = statisticService.getRemainingBudget(accountBookId);
//        return ResponseEntity.ok(remainingBudget);
//    }
//
//    /**
//     * 라인차트
//     */
//    @GetMapping("/statistics/category-by-dates")
//    public ResponseEntity<List<DateCategoryAmountDTO>> getStatisticsByCategoryAndDates(@RequestParam Long accountBookId, @RequestParam String category) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Long memberId = statisticService.getMemberIdByUsername(username);
//        List<DateCategoryAmountDTO> statistics; // 통계 저장하는곳
//
//        if ("ALL".equals(category)) { // ALL은 카테고리 상관없는 모든 통계 가져오는 것 (전체지출)
//            statistics = statisticService.getStatisticsByDates(accountBookId, memberId); // all category 날짜별 통계
//        } else { // 특정 카테고리에 대한 통계
//            List<Category> categories = Collections.singletonList(Category.valueOf(category));
//            statistics = statisticService.getStatisticsByCategoryAndDates(accountBookId, memberId, categories);
//        }
//
//        return ResponseEntity.ok(statistics);
//    }


    //마이 리포트
    @GetMapping("/myreport")
    public Map<String, Object> getMyReportData(@AuthenticationPrincipal CustomUserDetails userDetails){
        return statisticService.getMyReportData(userDetails.getUsername());
    }


    //가계부별 지출 통계
    //총 누적 지출
    @GetMapping("/total")
    public double getTotalExpenditure(@RequestParam Long accountBookId){
        return statisticService.getTotalExpenditureByAccountBook(accountBookId);
    }

    //총 예산
    @GetMapping("/total/budget")
    public double getTotalBudget(@RequestParam Long accountBookId){
        return statisticService.getTotalBudgetByAccountBook(accountBookId);
    }

    //가계부 카테고리별 총 지출
    @GetMapping("/total/category")
    public Map<String, Double> getExpenditureByCategory(@RequestParam Long accountBookId){
        return statisticService.getTotalExpenditureByAccountBookAndCategory(accountBookId);
    }

    //일자별 통계(카테고리)
    @GetMapping("/daily/category")
    public List<DateCategoryAmountDTO> getDailyCategoryExpense(@RequestParam Long accountBookId){
        return statisticService.getDailyCategoryExpense(accountBookId);
    }

    //일자별 통계(결제 방법)
    @GetMapping("/daily/paymentmethod")
    public List<PaymentMethodAmountDTO> getDailyPaymentMethodExpense(@RequestParam Long accountBookId, @RequestParam String date){
        return statisticService.getDailyPaymentMethodExpense(accountBookId, date);
    }

    //라인 차트(카테고리)
    @GetMapping("/daily/line-chart")
    public List<DateCategoryAmountDTO> getDailyCategoryExpenseForLineChart(@RequestParam Long accountBookId, @RequestParam String category){
        return statisticService.getDailyCategoryExpenseForLineChart(accountBookId, category);
    }


}