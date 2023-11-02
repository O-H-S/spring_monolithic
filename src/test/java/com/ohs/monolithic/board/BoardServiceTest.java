package com.ohs.monolithic.board;


import com.ohs.monolithic.board.service.BoardManageService;
import com.ohs.monolithic.board.repository.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
// 전달되는 클래스(MockitoExtension.class)는 Extension 인터페이스를 구현해야함.
// Extension 인터페이스는 여러 하위 인터페이스를 가지고 있고, 이를 통해 다양한 확장 포인트를 제공합니다.
/*
Extension 인터페이스와 하위 인터페이스
        BeforeAllCallback: 모든 테스트 메서드 전에 한 번 호출됩니다.
        BeforeEachCallback: 각 테스트 메서드 전에 호출됩니다.
        AfterEachCallback: 각 테스트 메서드 후에 호출됩니다.
        AfterAllCallback: 모든 테스트 메서드 후에 한 번 호출됩니다.
        ParameterResolver:
        */
// MockitoExtension 확장 기능을 테스트 클래스(BoardServiceTest)에 적용하며, 이로 인해 @Mock 어노테이션이 붙은 필드에 목 객체가 자동으로 생성되고 주입됩니다.
public class BoardServiceTest {


    @InjectMocks
    private BoardManageService testTarget;


    @Mock
    private BoardRepository mockRepository;

    @Test
    @DisplayName("새로운 게시판 등록")
    public void createNewBoard(){

        //given
        //doReturn(null).when(mockRepository).findByUserIdAndMembershipType(userId, membershipType);
        //doReturn(membership()).when(membershipRepository).save(any(Membership.class));

        /*// when
        final Membership result = target.addMembership(userId, membershipType, point);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);

        // verify
        verify(membershipRepository, times(1)).findByUserIdAndMembershipType(userId, membershipType);
        verify(membershipRepository, times(1)).save(any(Membership.class));
*/
    }

}
