package org.nervousync.database.beans.configs.reference;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;

/**
 * <h2 class="en-US">Reference join configure information</h2>
 * <h2 class="zh-CN">外键关联列配置信息</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Mar 30, 2016 17:48:56 $
 */
@XmlType(name = "join_config")
@XmlRootElement(name = "join_config")
@XmlAccessorType(XmlAccessType.NONE)
public final class JoinConfig extends BeanObject {
    /**
     * <span class="en-US">Serial version UID</span>
     * <span class="zh-CN">序列化UID</span>
     */
    private static final long serialVersionUID = -5091778742481427204L;
    /**
     * <span class="en-US">The field name in main table</span>
     * <span class="zh-CN">主表中的属性名称</span>
     */
    @XmlElement(name = "current_field")
    private String currentField;
    /**
     * <span class="en-US">The field name in reference table</span>
     * <span class="zh-CN">关联表中的属性名称</span>
     */
    @XmlElement(name = "reference_field")
    private String referenceField;

    /**
     * <h4 class="en-US">Constructor method for join configure information</h4>
     * <h4 class="zh-CN">外键关联列配置信息的构造方法</h4>
     */
    public JoinConfig() {
    }

    /**
     * <h4 class="en-US">Getter method for the field name in main table</h4>
     * <h4 class="zh-CN">主表中的属性名称的Getter方法</h4>
     *
     * @return <span class="en-US">The field name in main table</span>
     * <span class="zh-CN">主表中的属性名称</span>
     */
    public String getCurrentField() {
        return currentField;
    }

    /**
     * <h4 class="en-US">Setter method for the field name in main table</h4>
     * <h4 class="zh-CN">主表中的属性名称的Setter方法</h4>
     *
     * @param currentField <span class="en-US">The field name in main table</span>
     *                     <span class="zh-CN">主表中的属性名称</span>
     */
    public void setCurrentField(String currentField) {
        this.currentField = currentField;
    }

    /**
     * <h4 class="en-US">Getter method for the field name in reference table</h4>
     * <h4 class="zh-CN">关联表中的属性名称的Getter方法</h4>
     *
     * @return <span class="en-US">The field name in reference table</span>
     * <span class="zh-CN">关联表中的属性名称</span>
     */
    public String getReferenceField() {
        return referenceField;
    }

    /**
     * <h4 class="en-US">Setter method for the field name in reference table</h4>
     * <h4 class="zh-CN">关联表中的属性名称的Setter方法</h4>
     *
     * @param referenceField <span class="en-US">The field name in reference table</span>
     *                       <span class="zh-CN">关联表中的属性名称</span>
     */
    public void setReferenceField(String referenceField) {
        this.referenceField = referenceField;
    }
}
