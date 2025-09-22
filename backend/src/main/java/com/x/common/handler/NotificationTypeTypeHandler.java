package com.x.common.handler;

import com.x.common.enumeration.NotificationType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

public class NotificationTypeTypeHandler extends BaseTypeHandler<NotificationType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, NotificationType parameter, JdbcType jdbcType) throws SQLException {
        ps.setByte(i, parameter.getCode()); // 这里用 byte
    }

    @Override
    public NotificationType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte code = rs.getByte(columnName);
        return NotificationType.fromCode(code);
    }

    @Override
    public NotificationType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte code = rs.getByte(columnIndex);
        return NotificationType.fromCode(code);
    }

    @Override
    public NotificationType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte code = cs.getByte(columnIndex);
        return NotificationType.fromCode(code);
    }
}
