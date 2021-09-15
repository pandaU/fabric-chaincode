package org.hyperledger.fabric.samples.assettransfer.common;

import com.google.gson.annotations.SerializedName;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;


/**
 * <p>
 * The type Record.
 *
 * @author XieXiongXiong
 * @date 2021 -09-15
 */
@DataType
public class Record {

	/**
	 * Key
	 */
	@Property
	@SerializedName(value = "Key")
	private String key;

	/**
	 * Record
	 */
	@Property
	@SerializedName(value = "Record")
	private String record;

	/**
	 * Record
	 *
	 * @param key    key
	 * @param record record
	 */
	public Record(String key, String record) {
		this.key = key;
		this.record = record;
	}

	/**
	 * Gets key.
	 *
	 * @return the key
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:12
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets key.
	 *
	 * @param key the key
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:12
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets record.
	 *
	 * @return the record
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:12
	 */
	public String getRecord() {
		return record;
	}

	/**
	 * Sets record.
	 *
	 * @param record the record
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:12
	 */
	public void setRecord(String record) {
		this.record = record;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getKey(), getRecord());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}

		Record other = (Record) obj;

		return Objects.deepEquals(new Object[] { getKey(), getRecord() },
				new Object[] { other.getKey(), other.getRecord() });
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [key=" + key + ", record="
				+ record + "]";
	}

}
