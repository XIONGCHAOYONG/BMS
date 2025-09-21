package com.x.common.handler;

import com.x.common.enumeration.LendStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.*;

public class LendStatusTypeHandler extends BaseTypeHandler<LendStatus> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LendStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setByte(i, parameter.getCode()); // 枚举转数据库数字
    }

    @Override
    public LendStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte code = rs.getByte(columnName);
        return LendStatus.fromCode(code); // 数据库数字转枚举
    }

    @Override
    public LendStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte code = rs.getByte(columnIndex);
        return LendStatus.fromCode(code);
    }

    @Override
    public LendStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte code = cs.getByte(columnIndex);
        return LendStatus.fromCode(code);
    }
}
