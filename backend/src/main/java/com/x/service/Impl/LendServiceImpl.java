package com.x.service.Impl;


import com.x.common.context.BaseContext;
import com.x.common.enumeration.LendStatus;
import com.x.common.enumeration.NotificationType;
import com.x.common.exception.BusinessException;
import com.x.mapper.BookMapper;
import com.x.mapper.LendMapper;
import com.x.mapper.NotificationMapper;
import com.x.mapper.UserMapper;
import com.x.pojo.dto.LendDTO;
import com.x.pojo.dto.ReLendDTO;
import com.x.pojo.dto.ReturnDTO;
import com.x.pojo.entity.Lend;
import com.x.pojo.entity.User;
import com.x.pojo.vo.LendVO;
import com.x.service.LendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class LendServiceImpl implements LendService {
    @Autowired
    private LendMapper lendMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Override
    public List<LendVO> getAllLend() {
        return lendMapper.getAllLend(BaseContext.getCurrentId());
    }

    @Override
    public List<LendVO> getCurrentLend() {
        return lendMapper.getCurrentLend(LendStatus.UNRETURNED,LendStatus.RE_LEND,BaseContext.getCurrentId());
    }

    @Override
    @Transactional
    public void lendBook(LendDTO lendDTO) {
        User user = userMapper.getUserById(BaseContext.getCurrentId());
        if(user.getPoints()<=0) {
            throw new BusinessException("积分不足!");
        }
        Lend lend = new Lend();
        lend.setUserId(BaseContext.getCurrentId());
        lend.setBookId(lendDTO.getBookId());
        lend.setBorrowDate(LocalDate.now());
        lend.setDueDate(LocalDate.now().plusDays(lendDTO.getDays()));
        lend.setStatus(LendStatus.UNRETURNED);
        // 更新书籍的借阅数量
        bookMapper.increaseTotalLend(lendDTO.getBookId());
        //更新库存
        bookMapper.decreaseAvailableStock(lendDTO.getBookId());
        lendMapper.insert(lend);
    }


    @Override

    public void renewBook(ReLendDTO reLendDTO) {
        LocalDate dueDate=reLendDTO.getDueDate().plusDays(reLendDTO.getDays());
        lendMapper.renewBook(reLendDTO.getLendId(),dueDate,LendStatus.RE_LEND);
    }

    @Override
    @Transactional
    public void returnBook(ReturnDTO returnDTO) {
        // 更新库存
        bookMapper.increaseAvailableStock(returnDTO.getBookId());
        lendMapper.returnBook(returnDTO.getLendId(),LendStatus.RETURNED);
        //积分扣除
        if(returnDTO.getPoints()!=null && returnDTO.getPoints()>0) {
            userMapper.subPoints(BaseContext.getCurrentId(),returnDTO.getPoints());
        }
        //nnotification更改状态
        notificationMapper.updateStatus(BaseContext.getCurrentId(),returnDTO.getLendId(), NotificationType.RETUREND);


    }

    @Override
    public List<LendVO> getLendByBookId(Long bookId) {
        List<LendVO> lendVOS=lendMapper.getLendByBookId(BaseContext.getCurrentId(),bookId);
        System.out.println(lendVOS.size()+"asdkhufygasi");
        return  lendVOS;
    }
}
