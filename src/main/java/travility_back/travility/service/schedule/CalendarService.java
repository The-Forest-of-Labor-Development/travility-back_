package travility_back.travility.service.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import travility_back.travility.entity.AccountBook;
import travility_back.travility.entity.Member;
import travility_back.travility.repository.AccountBookRepository;
import travility_back.travility.repository.MemberRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final AccountBookRepository accountBookRepository;
    private final MemberRepository memberRepository;

    /**
     * <p>{@code getMemberByUsername()} username으로 memberId 조회</p>
     * <p>{@code getAccountBooksByMemberId()} 일정(event)를 조회하기 위해 memberId로 모든 일정 데이터 조회</p>
     * <p>{@code getAccountBooksEventsByUsername} 일정(event) 생성 및 동일한 일정 반복출력 제거</p>
     */

    // username으로 id찾기
    public Long getMemberIdByUsername(String username) {
        // username으로 member객체 찾기
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자 찾을 수 없음"));
        return member.getId();
    }

    // 사용자 id로 그 사용자의 모든 account_book 조회
    public List<AccountBook> getAccountBooksByMemberId(Long memberId) {
        // 리스트로 빼옴
        return accountBookRepository.findByMemberId(memberId);
    }

    // username으로 일정(event) 가져오기
    public List<Map<String, Object>> getAccountBooksEventsByUsername(String username) {
        Long memberId = getMemberIdByUsername(username);
        List<AccountBook> accountBooks = getAccountBooksByMemberId(memberId);

        // 예산 등록할 때 여러번 등록하면 같은 일정이 달력에 여러번 반복되어 출력됨. 그래서 Map으로 중복 제거
        Map<String, Map<String, Object>> uniqueEvents = new HashMap<>();

        // 일정(event) 생성
        for (AccountBook book : accountBooks) {
            // key : 시작날짜, 종료날짜
            String key = book.getStartDate().toString() + book.getEndDate().toString();
            if (!uniqueEvents.containsKey(key)) { // key가 중복되지 않았을 때만 일정을 추가해줌.
                Map<String, Object> event = new HashMap<>();
                event.put("title", book.getTitle()); // 제목 설정
                event.put("start", book.getStartDate().toString()); // 일정 시작 날짜 설정
                event.put("end", book.getEndDate().toString()); // 일정 종료 날짜 설정
                uniqueEvents.put(key, event); // Map에 넣어버리기
            }
        }

        // 추가한 일정을 리스트로 반환함
        return new ArrayList<>(uniqueEvents.values());
    }
}
