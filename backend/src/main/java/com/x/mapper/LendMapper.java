package com.x.mapper;

import com.x.common.enumeration.LendStatus;
import com.x.pojo.entity.Lend;
import com.x.pojo.vo.LendTaskVO;
import com.x.pojo.vo.LendVO;
import com.x.pojo.vo.TaskVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface LendMapper {



    List<LendVO> getAllLend(@Param("userId") Long userId);

    List<LendVO> getCurrentLend(@Param("unReturn") LendStatus unReturn,@Param("reLend") LendStatus reLend,@Param("userId") Long userId);

    List<LendVO> getLendByBookId(@Param("userId") Long userId,@Param("bookId") Long bookId);

    @Insert("insert into lend(user_id,book_id,borrow_date,due_date,status) values(#{userId},#{bookId},#{borrowDate},#{dueDate},#{status})")
    void insert(Lend lend);

    @Update("update lend set status = #{status},due_date = #{dueDate},renew_count " +
            "= renew_count+1 where lend_id=#{lendId}")
    void renewBook(@Param("lendId") Long lendId, @Param("dueDate") LocalDate dueDate, @Param("status") LendStatus status);

    @Update("update lend set status = #{status} where lend_id= #{lendId}")
    void returnBook(@Param("lendId") Long lendId,@Param("status") LendStatus  status);

    @Select("SELECT lend_id,due_date FROM lend where status = #{status}")
    List<LendTaskVO> findOldOverdue(@Param("status") LendStatus  status);

    @Select("SELECT lend_id,due_date FROM lend where status = #{status}")

    List<LendTaskVO> findOldSoonOverdue(@Param("status") LendStatus status);

    @Select("SELECT lend_id,user_id " +
            "FROM lend " +
            "WHERE DATEDIFF(due_date, #{today}) < 3 " +
            "AND DATEDIFF(due_date, #{today}) >= 0 " +
            "AND return_date IS NULL " +
            "AND status <> #{status}")
    List<TaskVO> findNewSoonOverdue(@Param("today") LocalDate today,
                                      @Param("status") LendStatus status);

    //将逾期和逾期的批量处理
    void batchUpdateStatus(List<Long> lendIds, LendStatus status);


//    List<Long> getOverDueIds(List<Long> ids);


}
