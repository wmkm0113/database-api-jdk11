/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.database.beans.configs.column;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Temporal;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.Globals;
import org.nervousync.database.commons.DatabaseUtils;
import org.nervousync.utils.ObjectUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Optional;

/**
 * <h2 class="en-US">Column information</h2>
 * <h2 class="zh-CN">列基本信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jun 27, 2018 23:02:27 $
 */
public final class ColumnInfo extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -2535643284257171857L;
    /**
     * <span class="en-US">Column name</span>
     * <span class="zh-CN">列名</span>
     */
    private final String columnName;
    /**
     * <span class="en-US">JDBC data type code</span>
     * <span class="zh-CN">JDBC数据类型代码</span>
     */
    private final int jdbcType;
    /**
     * <span class="en-US">Column is nullable</span>
     * <span class="zh-CN">列允许为空值</span>
     */
    private final boolean nullable;
    /**
     * <span class="en-US">Column length</span>
     * <span class="zh-CN">列长度</span>
     */
    private final int length;
    /**
     * <span class="en-US">Column precision</span>
     * <span class="zh-CN">列精度</span>
     */
    private final int precision;
    /**
     * <span class="en-US">Column scale</span>
     * <span class="zh-CN">列小数位数</span>
     */
    private final int scale;
    /**
     * <span class="en-US">Column default value</span>
     * <span class="zh-CN">列默认值</span>
     */
    private final Object defaultValue;

    /**
     * <h3 class="en-US">Private constructor method for column information</h3>
     * <h3 class="zh-CN">列基本信息的私有构造方法</h3>
     *
     * @param columnName   <span class="en-US">Column name</span>
     *                     <span class="zh-CN">列名</span>
     * @param jdbcType     <span class="en-US">JDBC data type code</span>
     *                     <span class="zh-CN">JDBC数据类型代码</span>
     * @param nullable     <span class="en-US">Column is nullable</span>
     *                     <span class="zh-CN">列允许为空值</span>
     * @param length       <span class="en-US">Column length</span>
     *                     <span class="zh-CN">列长度</span>
     * @param precision    <span class="en-US">Column precision</span>
     *                     <span class="zh-CN">列精度</span>
     * @param scale        <span class="en-US">Column scale</span>
     *                     <span class="zh-CN">列小数位数</span>
     * @param defaultValue <span class="en-US">Column default value</span>
     *                     <span class="zh-CN">列默认值</span>
     */
    private ColumnInfo(final String columnName, final int jdbcType, final boolean nullable,
                       final int length, final int precision, final int scale, final Object defaultValue) {
        this.columnName = columnName;
        this.jdbcType = jdbcType;
        this.nullable = nullable;
        this.length = length;
        this.precision = precision;
        this.scale = scale;
        this.defaultValue = defaultValue;
    }

    /**
     * <h3 class="en-US">Generate column information instance by given result set</h3>
     * <h3 class="zh-CN">根据给定的查询结果集生成列基本信息实例对象</h3>
     *
     * @param resultSet <span class="en-US">result set instance</span>
     *                  <span class="zh-CN">查询结果集实例对象</span>
     * @return <span class="en-US">Column information</span>
     * <span class="zh-CN">列基本信息</span>
     * @throws SQLException <span class="en-US">If an error occurs when parse result set instance</span>
     *                      <span class="zh-CN">如果解析查询结果集时出现异常</span>
     */
    public static ColumnInfo newInstance(final ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            throw new SQLException("ResultSet is null");
        }
        String columnName = resultSet.getString("COLUMN_NAME");
        int jdbcType = resultSet.getInt("DATA_TYPE");
        boolean nullable = ObjectUtils.nullSafeEquals("YES", resultSet.getString("IS_NULLABLE"));
        int length, precision, scale;
        switch (jdbcType) {
            case Types.CHAR:
            case Types.NCHAR:
            case Types.VARCHAR:
            case Types.NVARCHAR:
                length = resultSet.getInt("COLUMN_SIZE");
                precision = Globals.DEFAULT_VALUE_INT;
                scale = Globals.DEFAULT_VALUE_INT;
                break;
            case Types.DECIMAL:
            case Types.FLOAT:
            case Types.NUMERIC:
            case Types.DOUBLE:
                length = Globals.DEFAULT_VALUE_INT;
                precision = resultSet.getInt("COLUMN_SIZE");
                scale = resultSet.getInt("DECIMAL_DIGITS");
                break;
            default:
                length = Globals.DEFAULT_VALUE_INT;
                precision = Globals.DEFAULT_VALUE_INT;
                scale = Globals.DEFAULT_VALUE_INT;
                break;
        }
        return new ColumnInfo(columnName, jdbcType, nullable, length, precision, scale,
                Optional.ofNullable(resultSet.getObject("COLUMN_DEF"))
                        .map(Object::toString)
                        .orElse(Globals.DEFAULT_VALUE_STRING));
    }

    /**
     * <h3 class="en-US">Generate column information instance by given field instance and default value</h3>
     * <h3 class="zh-CN">根据给定的反射获得的属性实例对象和默认值生成列基本信息实例对象</h3>
     *
     * @param field        <span class="en-US">Field instance</span>
     *                     <span class="zh-CN">反射获得的属性实例对象</span>
     * @param defaultValue <span class="en-US">Default value of current field</span>
     *                     <span class="zh-CN">当前属性的默认值</span>
     * @return <span class="en-US">Generated column information instance</span>
     * <span class="zh-CN">生成的列基本信息实例对象</span>
     */
    public static ColumnInfo newInstance(final Field field, final Object defaultValue) {
        if (field == null) {
            return null;
        }
        Column column = field.getAnnotation(Column.class);
        String columnName = column.name().isEmpty() ? field.getName() : column.name();
        int precision = column.precision(), scale = column.scale(), jdbcType, length;
        Class<?> fieldType = field.getType();
        if (Date.class.equals(fieldType) && field.isAnnotationPresent(Temporal.class)) {
            switch (field.getAnnotation(Temporal.class).value()) {
                case DATE:
                    jdbcType = Types.DATE;
                    break;
                case TIME:
                    jdbcType = Types.TIME;
                    break;
                default:
                    jdbcType = Types.TIMESTAMP;
                    break;
            }
        } else if (field.isAnnotationPresent(Lob.class)) {
            if (String.class.equals(fieldType) || char[].class.equals(fieldType)
                    || Character[].class.equals(fieldType)) {
                jdbcType = Types.CLOB;
            } else {
                jdbcType = Types.BLOB;
            }
        } else {
            jdbcType = DatabaseUtils.jdbcType(fieldType);
        }
        switch (jdbcType) {
            case Types.CHAR:
            case Types.NCHAR:
            case Types.VARCHAR:
            case Types.NVARCHAR:
                length = column.length();
                break;
            default:
                length = Globals.DEFAULT_VALUE_INT;
                break;
        }
        return new ColumnInfo(columnName, jdbcType,
                (field.isAnnotationPresent(Id.class) ? Boolean.FALSE : column.nullable()),
                length, precision, scale, defaultValue);
    }

    /**
     * <h3 class="en-US">Check the given column information was modified</h3>
     * <h3 class="zh-CN">检查给定的列基本信息是否更改</h3>
     *
     * @param columnInfo <span class="en-US">Target column information</span>
     *                   <span class="zh-CN">目标列基本信息</span>
     * @return <span class="en-US">Check result</span>
     * <span class="zh-CN">检查结果</span>
     */
    public boolean modified(@Nonnull final ColumnInfo columnInfo) {
        return !ObjectUtils.nullSafeEquals(this.jdbcType, columnInfo.getJdbcType())
                || !ObjectUtils.nullSafeEquals(this.nullable, columnInfo.isNullable())
                || !ObjectUtils.nullSafeEquals(this.length, columnInfo.getLength())
                || !ObjectUtils.nullSafeEquals(this.precision, columnInfo.getPrecision())
                || !ObjectUtils.nullSafeEquals(this.scale, columnInfo.getScale())
                || !ObjectUtils.nullSafeEquals(this.defaultValue, columnInfo.getDefaultValue());
    }

    /**
     * <h3 class="en-US">Getter method for column name</h3>
     * <h3 class="zh-CN">列名的Getter方法</h3>
     *
     * @return <span class="en-US">Column name</span>
     * <span class="zh-CN">列名</span>
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * <h3 class="en-US">Getter method for JDBC data type code</h3>
     * <h3 class="zh-CN">JDBC数据类型代码的Getter方法</h3>
     *
     * @return <span class="en-US">JDBC data type code</span>
     * <span class="zh-CN">JDBC数据类型代码</span>
     */
    public int getJdbcType() {
        return jdbcType;
    }

    /**
     * <h3 class="en-US">Getter method for column is nullable</h3>
     * <h3 class="zh-CN">列允许为空值的Getter方法</h3>
     *
     * @return <span class="en-US">Column is nullable</span>
     * <span class="zh-CN">列允许为空值</span>
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * <h3 class="en-US">Getter method for column length</h3>
     * <h3 class="zh-CN">列长度的Getter方法</h3>
     *
     * @return <span class="en-US">Column length</span>
     * <span class="zh-CN">列长度</span>
     */
    public int getLength() {
        return length;
    }

    /**
     * <h3 class="en-US">Getter method for column precision</h3>
     * <h3 class="zh-CN">列精度的Getter方法</h3>
     *
     * @return <span class="en-US">Column precision</span>
     * <span class="zh-CN">列精度</span>
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * <h3 class="en-US">Getter method for column scale</h3>
     * <h3 class="zh-CN">列小数位数的Getter方法</h3>
     *
     * @return <span class="en-US">Column scale</span>
     * <span class="zh-CN">列小数位数</span>
     */
    public int getScale() {
        return scale;
    }

    /**
     * <h3 class="en-US">Getter method for column default value</h3>
     * <h3 class="zh-CN">列默认值的Getter方法</h3>
     *
     * @return <span class="en-US">Column default value</span>
     * <span class="zh-CN">列默认值</span>
     */
    public Object getDefaultValue() {
        return defaultValue;
    }
}
