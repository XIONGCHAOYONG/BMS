package com.x.service;

import com.x.pojo.dto.LendDTO;
import com.x.pojo.dto.ReLendDTO;
import com.x.pojo.dto.ReturnDTO;
import com.x.pojo.vo.LendVO;

import java.util.List;

public interface LendService {
    List<LendVO> getAllLend();

    List<LendVO> getCurrentLend();

    void lendBook(LendDTO lendDTO);

    void renewBook(ReLendDTO reLendDTO);

    void returnBook(ReturnDTO returnDTO);

    List<LendVO> getLendByBookId(Long bookId);
}
