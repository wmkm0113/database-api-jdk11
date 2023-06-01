package org.nervousync.database.entity.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
public abstract class BaseObject extends BeanObject implements Comparable<Object> {

	@JsonIgnore
	private boolean newObject = Boolean.TRUE;
	@JsonIgnore
	private Boolean forUpdate = null;
	@JsonIgnore
	private Long transactionalCode = null;
	@JsonIgnore
	private final List<String> modifiedColumns = new ArrayList<>();
	@JsonIgnore
	private final List<String> loadedFields = new ArrayList<>();

	public final boolean newObject() {
		return this.newObject;
	}

	public final boolean forUpdate() {
		return this.forUpdate != null && this.forUpdate;
	}

	public final List<String> modifiedColumns() {
		return this.modifiedColumns;
	}

	public final boolean dataModified() {
		return this.newObject() || (this.forUpdate() && this.modifiedColumns.size() > 0);
	}

	public final void setForUpdate(final Boolean forUpdate) {
		if (this.forUpdate == null) {
			this.forUpdate = forUpdate;
			this.newObject = Boolean.FALSE;
		}
	}

	public final Long getTransactionalCode() {
		return transactionalCode;
	}

	public final void setTransactionalCode(final Long transactionalCode) {
		if (this.transactionalCode == null) {
			this.transactionalCode = transactionalCode;
		}
	}

	public final boolean loadedField(final String fieldName) {
		return this.loadedFields.contains(fieldName);
	}

	public final void loadField(final String fieldName) {
		if (this.loadedFields.contains(fieldName)) {
			return;
		}
		this.loadedFields.add(fieldName);
	}

	public final void modifyField(final String fieldName) {
		if (this.modifiedColumns.contains(fieldName)) {
			return;
		}
		this.modifiedColumns.add(fieldName);
	}

	@SuppressWarnings("unchecked")
	public final int compareTo(Object o) {
		if (!(this.getClass().isAssignableFrom(o.getClass()))) {
			throw new ClassCastException("Cannot compare instance of " + getClass().getName()
					+ " to the instance of " + o.getClass().getName());
		}
		for (Field field : ReflectionUtils.getAllDeclaredFields(this.getClass())) {
			if (Comparable.class.isAssignableFrom(field.getType())) {
				String fieldName = field.getName();
				Object origValue = ReflectionUtils.getFieldValue(fieldName, this);
				Object destValue = ReflectionUtils.getFieldValue(fieldName, o);

				if (!Objects.equals(origValue, destValue)) {
					return ((Comparable<Object>)origValue).compareTo(destValue);
				}
			}
		}

		return Globals.INITIALIZE_INT_VALUE;
	}
}
