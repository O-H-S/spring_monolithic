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
  @Transactional(readOnly = true)
  public void validateWritePermission(Integer boardId, UserRole role, String method){
    if(role == UserRole.ADMIN)
      return;
    PermissionResponse response = getPermissionResponse(boardId, role);
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
}
