package travility_back.travility.service.statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import travility_back.travility.dto.statistics.*;
import travility_back.travility.entity.AccountBook;
import travility_back.travility.entity.Budget;
import travility_back.travility.entity.Expense;
import travility_back.travility.entity.Member;
import travility_back.travility.entity.enums.Category;
import travility_back.travility.entity.enums.PaymentMethod;
import travility_back.travility.repository.AccountBookRepository;
import travility_back.travility.repository.BudgetRepository;
import travility_back.travility.repository.ExpenseRepository;
import travility_back.travility.repository.MemberRepository;
import travility_back.travility.util.CalcUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final BudgetRepository budgetRepository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); // toString()대신 사용
    private final AccountBookRepository accountBookRepository;

    // 사용자 통계 데이터 가져오기
//    public MyReportExpenseStatisticsDTO getStatistics(Long memberId) {
//        MyReportExpenseStatisticsDTO myReportExpenseStatisticsDto = new MyReportExpenseStatisticsDTO();
//
//        // 지출 없어도 처리해서 뿌릴거에요
//        List<Object[]> categoryAmounts = expenseRepository.findTotalAmountByCategory(memberId); // 카테고리별 총액 조회
//        if (categoryAmounts.isEmpty()) { // 카테고리별 총액이 비어있으면
//            // 모두 빈 값으로 초기화
//            myReportExpenseStatisticsDto.setCategories(new String[]{});
//            myReportExpenseStatisticsDto.setAmounts(new double[]{});
//            myReportExpenseStatisticsDto.setPaymentMethods(new PaymentMethodAmountDTO[]{});
//            myReportExpenseStatisticsDto.setTotalAmount(0);
//            return myReportExpenseStatisticsDto;
//        }
//
//        // 카테고리와 각 카테고리별 지출액 반환
//        String[] categories = new String[categoryAmounts.size()];
//        double[] amounts = new double[categoryAmounts.size()];
//
//        for (int i = 0; i < categoryAmounts.size(); i++) {
//            categories[i] = ((Category) categoryAmounts.get(i)[0]).name(); // 카테고리 이름 설정
//            amounts[i] = (double) categoryAmounts.get(i)[1]; // 해당 카테고리 지출액 설정
//        }
//
//        // 결제 방법별 총액 조회할거에요
//        List<Object[]> paymentMethodAmounts = expenseRepository.findTotalAmountByPaymentMethod(memberId);
//        PaymentMethodAmountDTO[] paymentMethods = new PaymentMethodAmountDTO[paymentMethodAmounts.size()];
//
//        for (int i = 0; i < paymentMethodAmounts.size(); i++) {
//            PaymentMethodAmountDTO paymentMethodAmountDTO = new PaymentMethodAmountDTO();
//            paymentMethodAmountDTO.setPaymentMethod((PaymentMethod) paymentMethodAmounts.get(i)[0]); // 결제 방법 설정
//            paymentMethodAmountDTO.setAmount((double) paymentMethodAmounts.get(i)[1]); // 해당 결제 방법의 지출액 설정
//            paymentMethods[i] = paymentMethodAmountDTO;
//        }
//
//        // 전체 지출 총액 계산
//        double totalAmount = 0;
//        for (double amount : amounts) {
//            totalAmount += amount; // 각 카테고리 지출액 전부 더한 총액
//        }
//
//        // dto 설정값 저장하고싶어요
//        myReportExpenseStatisticsDto.setCategories(categories);
//        myReportExpenseStatisticsDto.setAmounts(amounts);
//        myReportExpenseStatisticsDto.setPaymentMethods(paymentMethods);
//        myReportExpenseStatisticsDto.setTotalAmount(totalAmount);
//
//        return myReportExpenseStatisticsDto;
//    }
//
//    /**
//     * 현재 인증된(로그인된) 사용자의 지출 통계 데이터 가져오는 메서드
//     * @return 현재 인증된 사용자의 지출 통계 데이터 DTO
//     */
//    public MyReportExpenseStatisticsDTO getStatistics() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        Long memberId = getMemberIdByUsername(username); // 사용자 이름으로 username가져오기
//
//        return getStatistics(memberId); // username으로 지출통계 가져오기
//    }
//
//    /**
//     * 사용자 이름으로 username 가져오는 메서드
//     * @param username 사용자 이름
//     * @return 사용자 ID
//     */
//    public Long getMemberIdByUsername(String username) {
//        Optional<Member> member = memberRepository.findByUsername(username); // 사용자 이름으로 사용자 검색
//        if (member.isPresent()) {
//            System.out.println("member.get().getId() = " + member.get().getId()); // 삭제
//            return member.get().getId(); // 사용자 ID
//        } else {
//            throw new UsernameNotFoundException(username); // 사용자 없으면
//        }
//    }
//
//    /**
//     * 사용자 이름으로 사용자 객체 가져오는 메서드
//     * @param username 사용자 이름
//     * @return 사용자 객체
//     */
//    public Member getMemberByUsername(String username) {
//        Optional<Member> member = memberRepository.findByUsername(username); // 사용자 이름으로 사용자 검색
//        if (member.isPresent()) {
//            return member.get(); // 객체 자체 반환
//        } else {
//            throw new UsernameNotFoundException(username); // 사용자 없으면
//        }
//    }
//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    /**
//     * 날짜별로 카테고리 지출 금액 가져오는 메서드
//     */
//    public List<DateCategoryAmountDTO> getStatisticsByDate(Long accountBookId, Long memberId) {
//        List<Object[]> results = expenseRepository.findTotalAmountByDateAndCategory(accountBookId, memberId);
//        return results.stream()
//                .map(result -> new DateCategoryAmountDTO(
//                        ((LocalDateTime) result[0]).format(DATE_TIME_FORMATTER),
//                        (Category) result[1],
//                        convertToDouble(result[2])
//                ))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 날짜별로 지출 방법별 금액 가져오기
//     */
//    public List<PaymentMethodAmountDTO> getPaymentMethodStatistics(Long accountBookId, Long memberId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
//        List<Object[]> results = expenseRepository.findTotalAmountByPaymentMethodAndDate(accountBookId, memberId, startOfDay, endOfDay);
//        return results.stream()
//                .map(result -> new PaymentMethodAmountDTO(
//                        formatDate(startOfDay.toLocalDate()), // 날짜 포매팅 방법 변경
//                        (PaymentMethod) result[0],
//                        convertToDouble(result[1])
//                ))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 한 일정에 대한 카테고리별 총 지출 가져오기
//     */
//    public List<DateCategoryAmountDTO> getTotalAmountByCategoryForAll(Long accountBookId, Long memberId) {
//        List<Object[]> results = expenseRepository.findTotalAmountByCategoryForAll(accountBookId, memberId);
//        return results.stream()
//                .map(result -> new DateCategoryAmountDTO(
//                        "TOTAL",
//                        (Category) result[0],
//                        convertToDouble(result[1])
//                ))
//                .collect(Collectors.toList());
//    }
//
//    // 예산 - 지출
//    public Double getTotalBudgetByAccountBookId(Long accountBookId) {
//        return budgetRepository.getTotalBudgetByAccountBookId(accountBookId);
//    }
//
//    public Double getTotalExpenseByAccountBookId(Long accountBookId) {
//        return expenseRepository.getTotalExpenseByAccountBookId(accountBookId);
//    }
//
//    public Double getRemainingBudget(Long accountBookId) {
//        Double totalBudget = getTotalBudgetByAccountBookId(accountBookId);
//        Double totalExpense = getTotalExpenseByAccountBookId(accountBookId);
//        return totalBudget - totalExpense;
//    }
//
//    /**
//     * 라인차트
//     * 사용자의 특정 가계부에 대한 날짜별 총 지출 금액 조회 (전체)
//     */
//    public List<DateCategoryAmountDTO> getStatisticsByDates(Long accountBookId, Long memberId) {
//        List<Object[]> results = expenseRepository.findTotalAmountByDates(accountBookId, memberId);
//        return results.stream()
//                .map(result -> new DateCategoryAmountDTO(
//                        ((LocalDateTime) result[0]).format(DATE_TIME_FORMATTER),
//                        null, // 전체라 카테고리 상관없음
//                        convertToDouble(result[1])
//                ))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 라인차트
//     * 사용자의 특정 가계부에 대한 날짜별 + 카테고리별 총 지출 금액 조회
//     */
//    public List<DateCategoryAmountDTO> getStatisticsByCategoryAndDates(Long accountBookId, Long memberId, List<Category> categories) {
//        List<Object[]> results = expenseRepository.findTotalAmountByDatesAndCategories(accountBookId, memberId, categories);
//        return results.stream()
//                .map(result -> new DateCategoryAmountDTO(
//                        ((LocalDateTime) result[0]).format(DATE_TIME_FORMATTER),
//                        (Category) result[1],
//                        convertToDouble(result[2])
//                ))
//                .collect(Collectors.toList());
//    }
//
//    // Integer -> Double 캐스팅
//    private Double convertToDouble(Object value) {
//        if (value instanceof Integer) {
//            return ((Integer) value).doubleValue();
//        } else if (value instanceof Double) {
//            return (Double) value;
//        } else {
//            throw new ClassCastException("int -> double 캐스팅 오류 : StatisticService : " + value.getClass().getName());
//        }
//    }
//
//    private String formatDate(LocalDate date) {
//        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
//    }


    //마이 리포트

    //카테고리별 지출 초기화
    private Map<String, Double> initializeCategoryToTotalAmount() {
        Map<String, Double> categoryToTotalAmount = new HashMap<>();
        for (Category category : Category.values()) {
            categoryToTotalAmount.put(category.toString(), 0.0);
        }
        return categoryToTotalAmount;
    }

    //결제방법별 지출 초기화
    private Map<String, Double> initializePaymentMethodToTotalAmount() {
        Map<String, Double> paymentMethodToTotalAmount = new HashMap<>();
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            paymentMethodToTotalAmount.put(paymentMethod.toString(), 0.0);
        }
        return paymentMethodToTotalAmount;
    }

    //카테고리별 지출
    public Map<String, Double> calculateExpenseByCategory(List<Expense> expenses, Map<String, Double> currencyToAvgExchangeRate) {
        Map<String, Double> categoryToTotalAmount = initializeCategoryToTotalAmount();
        for (Expense expense : expenses) {
            String currency = expense.getCurUnit(); //통화 코드
            String category = expense.getCategory().toString(); //카테고리
            double amountInKRW = currencyToAvgExchangeRate.get(currency) * expense.getAmount(); //원화 지출
            categoryToTotalAmount.put(category, categoryToTotalAmount.getOrDefault(category, 0.0) + amountInKRW); //카테고리별 지출
        }
        return categoryToTotalAmount;
    }

    //결제방법별 지출
    public Map<String, Double> calculateExpenseByPaymentMethod(List<Expense> expenses, Map<String, Double> currencyToAvgExchangeRate) {
        Map<String, Double> paymentMethodToTotalAmount = initializePaymentMethodToTotalAmount();
        for (Expense expense : expenses){
            String currency = expense.getCurUnit(); //통화 코드
            String paymentMethod = expense.getPaymentMethod().toString();
            double amountInKRW = currencyToAvgExchangeRate.get(currency) * expense.getAmount(); //원화 지출
            paymentMethodToTotalAmount.put(paymentMethod, paymentMethodToTotalAmount.getOrDefault(paymentMethod, 0.0) + amountInKRW);
        }
        return paymentMethodToTotalAmount;
    }

    //마이 리포트 데이터
    public Map<String, Object> getMyReportData(String username){
        //회원 찾기
        Member member = memberRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Member Not found"));

        //해당 회원이 가진 가계부 리스트
        List<AccountBook> accountBooks = accountBookRepository.findByMemberId(member.getId());

        double totalExpenditure = 0; //총 누적 지출
        Map<String, Double> expenditureByCategory = initializeCategoryToTotalAmount(); //카테고리별 지출
        Map<String, Double> expenditureByPaymentMethod = initializePaymentMethodToTotalAmount();; //결제방법별 지출

        for (AccountBook accountBook : accountBooks){
            //해당 가계부 가중 평균 환율
            Map<String, Double> currencyToAvgExchangeRate = CalcUtil.calculateWeightedAverageExchangeRateByCurrency(accountBook.getBudgets());

            //해당 가계부 내 모든 지출 내역
            List<Expense> expenses = accountBook.getExpenses();

            //해당 가계부 카테고리별 지출
            Map<String, Double> categoryExpenses = calculateExpenseByCategory(expenses, currencyToAvgExchangeRate); //해당 가계부 카테고리별 지출
            for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()){
                expenditureByCategory.put(entry.getKey(), expenditureByCategory.get(entry.getKey()) + entry.getValue());
            }

            //해당 가계부 결제방법별 지출
            Map<String, Double> paymentMethodExpenses = calculateExpenseByPaymentMethod(expenses, currencyToAvgExchangeRate); //해당 가계부 결제방법별 지출
            for (Map.Entry<String, Double> entry : paymentMethodExpenses.entrySet()){
                expenditureByPaymentMethod.put(entry.getKey(), expenditureByPaymentMethod.get(entry.getKey()) + entry.getValue());
            }

            //해당 가계부 총 누적 지출
            totalExpenditure += CalcUtil.calculateTotalExpenses(accountBook);
        }

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("totalExpenditure", totalExpenditure);
        reportData.put("expenditureByCategory", expenditureByCategory);
        reportData.put("expenditureByPaymentMethod", expenditureByPaymentMethod);

        return reportData;
    }



    //지출 통계

    //총 지출
    public double getTotalExpenditureByAccountBook(Long accountBookId){
        AccountBook accountBook = accountBookRepository.findById(accountBookId).orElseThrow(()-> new NoSuchElementException("AccountBook not found"));
        return CalcUtil.calculateTotalExpenses(accountBook);
    }

    //총 예산
    public double getTotalBudgetByAccountBook(Long accountBookId) {
        AccountBook accountBook = accountBookRepository.findById(accountBookId).orElseThrow(()-> new NoSuchElementException("AccountBook not found"));
        Map<String, Double> currencyToAvgExchangeRate = CalcUtil.calculateWeightedAverageExchangeRateByCurrency(accountBook.getBudgets()); //통화 코드 별 가중 평균 환율

        double totalBudget = 0;
        for (Budget budget : accountBook.getBudgets()){
            String currency = budget.getCurUnit(); //통화 코드
            double amount = budget.getAmount(); //예산 금액
            totalBudget += currencyToAvgExchangeRate.get(currency) * amount;
        }

        return  totalBudget;
    }

    //총 카테고리별 지출
    public Map<String, Double> getTotalExpenditureByAccountBookAndCategory(Long accountBookId){
        //가계부 찾기
        AccountBook accountBook = accountBookRepository.findById(accountBookId).orElseThrow(()-> new NoSuchElementException("AccountBook not found"));

        //가중 평균 환율
        Map<String, Double> currencyToAvgExchangeRate = CalcUtil.calculateWeightedAverageExchangeRateByCurrency(accountBook.getBudgets());

        //카테고리별 지출
        return calculateExpenseByCategory(accountBook.getExpenses(), currencyToAvgExchangeRate); //해당 날짜의 카테고리별 지출
    }


    //일자별 통계 (지출 항목)
    public List<DateCategoryAmountDTO> getDailyCategoryExpense(Long accountBookId){
        //가계부 찾기
        AccountBook accountBook = accountBookRepository.findById(accountBookId).orElseThrow(()-> new NoSuchElementException("AccountBook not found"));

        //가중 평균 환율
        Map<String, Double> currencyToAvgExchangeRate = CalcUtil.calculateWeightedAverageExchangeRateByCurrency(accountBook.getBudgets());

        List<DateCategoryAmountDTO> dateCategoryAmountDTOs = new ArrayList<>();

        for(Expense expense : accountBook.getExpenses()){
            String currency = expense.getCurUnit(); //통화 코드
            double amount = expense.getAmount();
            double amountInKRW = currencyToAvgExchangeRate.get(currency) * amount;

            DateCategoryAmountDTO dateCategoryAmountDTO = new DateCategoryAmountDTO();
            dateCategoryAmountDTO.setDate(expense.getExpenseDate().toString());
            dateCategoryAmountDTO.setCategory(expense.getCategory());
            dateCategoryAmountDTO.setAmount(amountInKRW);

            dateCategoryAmountDTOs.add(dateCategoryAmountDTO);
        }

        return dateCategoryAmountDTOs;

    }

    //일자별 통계(결제 방법)
    public List<PaymentMethodAmountDTO> getDailyPaymentMethodExpense(Long accountBookId, String date){
        //가계부 찾기
        AccountBook accountBook = accountBookRepository.findById(accountBookId).orElseThrow(()-> new NoSuchElementException("AccountBook not found"));

        //해당 날짜 지출 목록
        LocalDate expenseDate = LocalDate.parse(date);
        LocalDateTime startOfDay = expenseDate.atStartOfDay();
        LocalDateTime endOfDay = expenseDate.plusDays(1).atStartOfDay();
        List<Expense> expenses = expenseRepository.findDailyAmountByPaymentMethod(accountBookId, startOfDay, endOfDay);

        //가중 평균 환율
        Map<String, Double> currencyToAvgExchangeRate = CalcUtil.calculateWeightedAverageExchangeRateByCurrency(accountBook.getBudgets());

        List<PaymentMethodAmountDTO> paymentMethodAmountDTOs = new ArrayList<>();

        double card = 0;
        double cash = 0;
        for(Expense expense : expenses){
            String currency = expense.getCurUnit();
            double amount = expense.getAmount();
            double amountInKRW = currencyToAvgExchangeRate.get(currency) * amount;

            if (expense.getPaymentMethod().toString().equals("CARD")){ //카드일 경우
                card += amountInKRW;
            }else{ //현금일 경우
                cash += amountInKRW;
            }
        }

        PaymentMethodAmountDTO cardDTO = new PaymentMethodAmountDTO();
        PaymentMethodAmountDTO cashDTO = new PaymentMethodAmountDTO();

        //카드
        cardDTO.setDate(date);
        cardDTO.setPaymentMethod(PaymentMethod.CARD);
        cardDTO.setAmount(card);

        paymentMethodAmountDTOs.add(cardDTO);

        //현금
        cashDTO.setDate(date);
        cashDTO.setPaymentMethod(PaymentMethod.CASH);
        cashDTO.setAmount(cash);

        paymentMethodAmountDTOs.add(cashDTO);

        return paymentMethodAmountDTOs;
    }

    //라인 차트 (카테고리)
    public List<DateCategoryAmountDTO> getDailyCategoryExpenseForLineChart(Long accountBookId, String category){
        //가계부 찾기
        AccountBook accountBook = accountBookRepository.findById(accountBookId).orElseThrow(()-> new NoSuchElementException("AccountBook not found"));

        //가중 평균 환율
        Map<String, Double> currencyToAvgExchangeRate = CalcUtil.calculateWeightedAverageExchangeRateByCurrency(accountBook.getBudgets());

        List<DateCategoryAmountDTO> dateCategoryAmountDTOS = new ArrayList<>();

        if (category.equals("ALL")){
            return getDailyCategoryExpense(accountBookId);
        }else{
            List<Expense> expenses = expenseRepository.findDailyAmountByCategoryForLineChart(accountBookId, Category.valueOf(category));

            for (Expense expense : expenses){
                String currency = expense.getCurUnit();
                double amount = expense.getAmount();
                double amountInKRW = currencyToAvgExchangeRate.get(currency) * amount;

                DateCategoryAmountDTO dateCategoryAmountDTO = new DateCategoryAmountDTO();

                dateCategoryAmountDTO.setDate(expense.getExpenseDate().toString());
                dateCategoryAmountDTO.setCategory(expense.getCategory());
                dateCategoryAmountDTO.setAmount(amountInKRW);

                dateCategoryAmountDTOS.add(dateCategoryAmountDTO);
            }

        }
        return dateCategoryAmountDTOS;
    }

}