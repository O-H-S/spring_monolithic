package com.ohs.monolithic.board.exception;

import com.ohs.monolithic.common.exception.BusinessException;
import com.ohs.monolithic.common.exception.ErrorCode;
import lombok.Getter;
@Getter
public class BoardPermissionException extends BusinessException {
  Integer boardId;
  String permissionName;
  public BoardPermissionException(Integer boardId, String permissionName){
    super(ErrorCode.NO_BOARD_PERMISSION);
    this.boardId = boardId;
    this.permissionName = permissionName;
  }

}

