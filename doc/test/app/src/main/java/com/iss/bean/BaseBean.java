package com.iss.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;
import com.iss.db.TableColumn;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Base Bean
 *
 * @param <T>
 */
public abstract class BaseBean<T> implements Serializable {

    /**
     * _id 字段
     */
    @TableColumn(type = TableColumn.Types.INTEGER, isPrimary = true)
    public static final String _ID = "_id";

    private static final long serialVersionUID = -804757173578073135L;

    /**
     * 将json对象转化为Bean实例
     *
     * @param jsonObj jsonObj
     * @return 实例
     */
    public abstract T parseJSON(JSONObject jsonObj);

    /**
     * 将Bean实例转化为json对象
     *
     * @return json
     */
    public abstract JSONObject toJSON();

    /**
     * 将数据库的cursor转化为Bean实例（如果对象涉及在数据库存取，需实现此方法）
     *
     * @param cursor cursor
     * @return bean
     */
    public abstract T cursorToBean(Cursor cursor);

    /**
     * 将Bean实例转化为一个ContentValues实例，供存入数据库使用（如果对象涉及在数据库存取，需实现此方法）
     *
     * @return 数据库 values
     */
    public abstract ContentValues beanToValues();

    /**
     * 转换为数据库 values
     *
     * @return 数据库 values
     */
    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        try {
            Class<?> c = getClass();
            Field[] fields = c.getFields();
            for (Field f : fields) {
                f.setAccessible(true);
                final TableColumn tableColumnAnnotation = f.getAnnotation(TableColumn.class);
                if (tableColumnAnnotation != null) {
                    if (tableColumnAnnotation.type() == TableColumn.Types.INTEGER) {
                        values.put(f.getName(), f.getInt(this));
                    } else if (tableColumnAnnotation.type() == TableColumn.Types.BLOB) {
                        values.put(f.getName(), (byte[]) f.get(this));
                    } else {
                        values.put(f.getName(), f.get(this).toString());
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            ALog.printStackTrace(e);
        } catch (IllegalAccessException e) {
            ALog.printStackTrace(e);
        }
        return values;
    }

    /**
     * 添加数据
     *
     * @param values values
     * @param key    key
     * @param value  value
     */
    protected void putContentValue(ContentValues values, String key, String value) {
        if (!TextUtils.isEmpty(value)) {
            values.put(key, value);
        }
    }

    /**
     * 添加数据
     *
     * @param values values
     * @param key    key
     * @param value  value
     */
    protected void putContentValue(ContentValues values, String key, int value) {
        if (value != 0) {
            values.put(key, value);
        }
    }

    /**
     * 添加数据
     *
     * @param values       values
     * @param key          key
     * @param value        value
     * @param defaultValue defaultValue
     */
    protected void putContentValue(ContentValues values, String key, long value, int defaultValue) {
        if (value != defaultValue) {
            values.put(key, value);
        }
    }

    /**
     * 添加数据
     *
     * @param values values
     * @param key    key
     * @param value  value
     */
    protected void putContentValueNotNull(ContentValues values, String key, String value) {
        if (value != null) {
            values.put(key, value);
        }
    }
}
