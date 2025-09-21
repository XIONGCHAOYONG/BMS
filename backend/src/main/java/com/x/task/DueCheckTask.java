package com.x.task;

import com.x.mapper.LendMapper;
import com.x.common.enumeration.LendStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DueCheckTask {

@Autowired
private LendMapper lendMapper;

//TODO 执行日志

    @Scheduled(cron = "0 0 0 * * ?")  //每天凌晨0点执行
    public void checkAndUpdateOverdueLends() {
        over();
    }

    @Scheduled(cron = "0 0 0 * * ?")
public void checkAndUpdateSoonOverdueLends() {
       soon();
    }


    //应用启动时运行一次
    @EventListener(ApplicationReadyEvent.class)
    public void checkAndUpdateSoonOverdueLendsStart() {
        soon();
    }
    @EventListener(ApplicationReadyEvent.class)
    public void checkAndUpdateOverdueLendsStart() {
       over();
    }

    private void over(){

        LocalDate now = LocalDate.now();

        // 查询所有逾期
        List<Long> overdueIds = lendMapper.findOverdueLendIds(now, LendStatus.OVERDUE);

        if (!overdueIds.isEmpty()) {
            // 更新这些记录的状态
            lendMapper.batchUpdateStatus(overdueIds, LendStatus.OVERDUE);
        }
    }

    private void soon(){
        LocalDate now = LocalDate.now();
        // 查询所有即将逾期
        List<Long> soonOverdueIds = lendMapper.findSoonOverdueLendIds(now, LendStatus.SOON_OVERDUE);
        if (!soonOverdueIds.isEmpty()) {
            lendMapper.batchUpdateStatus(soonOverdueIds, LendStatus.SOON_OVERDUE);
        }
    }
}
