package com.x.mapper;

import com.x.common.enumeration.NotificationType;
import com.x.pojo.entity.Notification;
import com.x.pojo.vo.LendTaskVO;
import com.x.pojo.vo.NotificationVO;
import com.x.utils.Result;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NotificationMapper {

    List<NotificationVO> getAllNotification(Long currentId);

    @Insert("INSERT INTO notification(user_id,days,book_id,type) VALUES(#{userId},#{days},#{bookId},#{type})")
    void insert(Notification notification);

    void resetDays(List<LendTaskVO> lendList);

    void upDays(List<LendTaskVO> lendList);

    void  downDays(List<LendTaskVO> lendList);

    void  updateType(List<Long> lendIds, NotificationType type);

    void  batchInsert(List<Notification> notifications);

    @Update("update notification SET type = #{notificationType} WHERE lend_id = #{lendId} and user_id = #{userId}")
    void updateStatus(Long userId,Long lendId,NotificationType notificationType);
}
