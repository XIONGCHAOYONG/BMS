package com.x.task;

import com.x.common.enumeration.NotificationType;
import com.x.mapper.LendMapper;
import com.x.common.enumeration.LendStatus;
import com.x.mapper.NotificationMapper;
import com.x.mapper.UserMapper;
import com.x.pojo.entity.Notification;
import com.x.pojo.vo.LendTaskVO;
import com.x.pojo.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class dueCheckTask {

    @Autowired
    private LendMapper lendMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

//TODO 执行日志

    @Scheduled(cron = "0 0 0 * * ?")  //每天凌晨0点执行
    //定时任务，每天凌晨自动执行
    public void autoDoTask() {
        doNotificationAndLend();
    }

    @Scheduled(cron = "0 0 0 1 * ?")  //每个月自动恢复积分
    public void recoverPoints(){
        userMapper.recoverPoints();
    }

    //每次启动后自动执行一遍
    @EventListener(ApplicationReadyEvent.class)
    public void startDoTask(){
        doNotificationAndLend();
    }



    //任务逻辑
    @Transactional
    public void doNotificationAndLend() {
        //1.实现逾期和将要逾期状态的修改
        // 2.实现通知的生成和更新
        //借阅是先将要逾期，后逾期。所以先处理将要逾期的相关操作
        //先获得原有的将要逾期ids  //获得所有逾期ids
        List<LendTaskVO> oldSoonOverdueLend = lendMapper.findOldSoonOverdue(LendStatus.SOON_OVERDUE);
        List<LendTaskVO> oldOverdueLend = lendMapper.findOldOverdue(LendStatus.OVERDUE);
        //在原有的将要逾期信息中筛选出逾期的
        List<LendTaskVO> newOverdueLend =oldSoonOverdueLend.stream()
                .filter(task -> task.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
        if (!newOverdueLend.isEmpty()){
        //修改状态为逾期、重置通知天数为1
            List<Long> newOverdueLendids = newOverdueLend.stream().map(LendTaskVO::getLendId).toList();
            lendMapper.batchUpdateStatus(newOverdueLendids, LendStatus.OVERDUE);
            notificationMapper.updateType(newOverdueLendids, NotificationType.OVERDUE);
            notificationMapper.resetDays(newOverdueLend);
    }
        //获得原来的将要逾期信息
        oldSoonOverdueLend.removeAll(newOverdueLend);
        List<LendTaskVO> soonOverdueLend = oldSoonOverdueLend;
        //对oldOverdueLendIds和SoonOverdueLendIds的天数进行修改+1和-1
        if(!oldOverdueLend.isEmpty()){
            notificationMapper.upDays(oldOverdueLend);
        }
        if(!soonOverdueLend.isEmpty()){
            notificationMapper.downDays(soonOverdueLend);
        }

        //再获得新的将要逾期信息  生成通知、改变状态
        List<TaskVO> newSoonOverdueLendIds = lendMapper.findNewSoonOverdue(LocalDate.now(), LendStatus.SOON_OVERDUE);
        List<Long> ids=new ArrayList<>();
        for (TaskVO item : newSoonOverdueLendIds) {
            ids.add(item.getLendId());
        }
        if(!newSoonOverdueLendIds.isEmpty()){
            lendMapper.batchUpdateStatus(ids, LendStatus.SOON_OVERDUE);
        }
        //批量生成通知
        if(!newSoonOverdueLendIds.isEmpty()){
            List<Notification> notifications = new ArrayList<>();
            for (TaskVO item : newSoonOverdueLendIds) {
                Notification notification = new Notification(null, item.getUserId(), item.getLendId(), 2, NotificationType.SOONOVER_DUE);
                notifications.add(notification);
            }
            notificationMapper.batchInsert(notifications);
        }


    }

}
