package com.ohs.monolithic.board.service;


import com.ohs.monolithic.account.domain.UserRole;
import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.board.domain.constants.BoardPaginationType;
import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.BoardCreationForm;
import com.ohs.monolithic.board.dto.BoardResponse;

import com.ohs.monolithic.board.repository.BoardRepository;
import com.ohs.monolithic.common.exception.DataNotFoundException;
import com.ohs.monolithic.common.exception.PermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BoardService {

  final BoardRepository boardRepository;
  final BoardPermissionService boardPermissionService;
  final BoardInternalService boardInternalService;

  //---------------------------------------------
  @Transactional
  public BoardResponse createBoard(String title, String desc){
    return boardInternalService.createBoardInternal(title, desc, BoardPaginationType.Offset_CountCache_CoveringIndex);
  }

  @Transactional
  public BoardResponse createBoard(String title, String desc,BoardPaginationType paginationType){
    return boardInternalService.createBoardInternal(title, desc, paginationType);
  }

  @Transactional
  public BoardResponse updateBoard(Integer boardId, BoardCreationForm form, AppUser appUser){
    if(!appUser.isAdmin()){
      throw new PermissionException("게시판 수정 권한이 없습니다");
    }
    return boardInternalService.updateBoardInternal(boardId, form);
  }
  @Transactional
  public void deleteBoard(Integer id, AppUser appUser) {
    if(!appUser.isAdmin()){
      throw new PermissionException("게시판 삭제 권한이 없습니다");
    }
    boardInternalService.deleteBoardInternal(id);
  }


  @Transactional(readOnly = true)
  public List<BoardResponse> getBoardsReadOnly() {
    return getBoardsReadOnly(true, true);
  }

  @Transactional(readOnly = true) // permission 체크 안함, DB CALL 무조건 발생
  public List<BoardResponse> getBoardsReadOnly(boolean includeTitle, boolean includeDesc) {
    List<BoardResponse> result = boardRepository.getAllBoards(includeTitle, includeDesc);

    List<Integer> boardIds = result.stream().map(BoardResponse::getId).collect(Collectors.toList());
    List<Long> postCounts = boardInternalService.getPostCounts(boardIds);

    for (int i = 0; i < result.size(); i++) {
      result.get(i).setPostCounts(postCounts.get(i));
    }
    return result;
  }

  @Transactional(readOnly = true)
  public BoardResponse getBoardReadOnly(Integer id, AppUser user) throws DataNotFoundException {

    BoardResponse response = boardInternalService.getBoardInternal(id);
    response.setPostCounts(boardInternalService.getPostCount(id));
    if(user == null)
      return response;

    var permissionResponse = boardPermissionService.getPermissionResponse(id, user.isAdmin()? UserRole.ADMIN:UserRole.USER);
    response.setWritable(permissionResponse.isRoleWritable);
    response.setWritableMethods(permissionResponse.writableMethods);

    return response;

  }



}
