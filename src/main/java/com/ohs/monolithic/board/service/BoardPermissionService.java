package com.ohs.monolithic.board.service;


import com.ohs.monolithic.account.domain.UserRole;
import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.BoardPermission;
import com.ohs.monolithic.board.exception.BoardPermissionException;
import com.ohs.monolithic.board.repository.BoardPermissionRepository;
import com.ohs.monolithic.board.repository.BoardRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 자주 사용되는 서비스이므로 캐시 구현해야함.
@Service
@RequiredArgsConstructor
public class BoardPermissionService {
  final BoardPermissionRepository boardPermissionRepository;
  final BoardRepository boardRepository;

  // 게시판 쓰기 권한을 검사한다.
  @Transactional(readOnly = true)
  public void validateWritePermission(Integer boardId, UserRole role, String method){
    // 어드민이면 허용
    if(role == UserRole.ADMIN)
      return;
    // 특정 role에 대한 게시판 권한을 조회한다.
    PermissionResponse response = getPermissionResponse(boardId, role);
    // 조회 결과를 통해,
    String reasonForWrite = response.getReasonFailedToWrite(method);
    if(reasonForWrite != null)
      throw new BoardPermissionException(boardId, reasonForWrite);
  }
  @AllArgsConstructor
  static public class PermissionResponse{

    UserRole role;
    public Boolean isRoleWritable;
    public Set<String> writableMethods;

    public String getReasonFailedToWrite(String method){
      if(!isRoleWritable)
        return "post_write_role";

      if(writableMethods.isEmpty() || writableMethods.contains("*"))
        return null;

      if(!writableMethods.contains(method))
        return "post_write_method";

      return null;
    }

  }
  @Transactional(readOnly = true)
  public PermissionResponse getPermissionResponse(Integer boardId, UserRole role){
    if(role == UserRole.ADMIN)
      return new PermissionResponse(role, Boolean.TRUE, Collections.singleton("*"));

    List<BoardPermission> permissionList = boardPermissionRepository.findByBoardAndNameLike(boardRepository.getReferenceById(boardId), "post_write_%");

    Set<String> writeRoles = new HashSet<>();
    Set<String> writeMethods = new HashSet<>();
    for(BoardPermission permission : permissionList){
      switch (permission.getName()) {
        case "post_write_role" -> {
          writeRoles.add(permission.getValue());
        }
        case "post_write_method" -> {
          writeMethods.add(permission.getValue());
        }
      }
    }
    if(!writeRoles.contains(role.toString())){
      return new PermissionResponse(role, Boolean.FALSE, writeMethods);
    }

    if(writeMethods.isEmpty())
      writeMethods.add("*");

    return new PermissionResponse(role, Boolean.TRUE, writeMethods);
  }

  @Transactional
  public void addWritePermission(Integer id, UserRole role, String method){
    BoardPermission writablePermission = boardPermissionRepository.findByBoardAndNameAndValue(boardRepository.getReferenceById(id), "post_write_role", role.toString());
    if(writablePermission == null) {
      writablePermission = BoardPermission.builder()
              .board(boardRepository.getReferenceById(id))
              .name("post_write_role")
              .value(role.toString())
              .build();
      boardPermissionRepository.save(writablePermission);
    }
    List<BoardPermission> methodPermissionList = boardPermissionRepository.findByBoardAndNameLike(boardRepository.getReferenceById(id), "post_write_method");
    if(methodPermissionList.isEmpty())
      return;
    for(BoardPermission p : methodPermissionList){
      String existMethod = p.getValue();
      if(existMethod.equals("*") || existMethod.equals(method))
        return;
    }
    boardPermissionRepository.save(BoardPermission.builder()
            .board(boardRepository.getReferenceById(id))
            .name("post_write_method")
            .value(method)
            .build());
  }
}
