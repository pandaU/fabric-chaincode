package org.hyperledger.fabric.samples.assettransfer.common;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

/**
 * <p>
 * The type History.
 *
 * @author XieXiongXiong
 * @date 2021 -09-15
 */
@DataType
public class History {

	/**
	 * Tx id
	 */
	@Property
	private String txId;

	/**
	 * Value
	 */
	@Property
	private String value;

	/**
	 * Timestamp
	 */
	@Property
	private Long timestamp;

	/**
	 * Is delete
	 */
	@Property
	private Boolean isDelete;

	/**
	 * Gets tx id.
	 *
	 * @return the tx id
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:12
	 */
	public String getTxId() {
		return txId;
	}

	/**
	 * Sets tx id.
	 *
	 * @param txId the tx id
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:12
	 */
	public void setTxId(String txId) {
		this.txId = txId;
	}

	/**
	 * Gets value.
	 *
	 * @return the value
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:12
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets value.
	 *
	 * @param value the value
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:12
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets timestamp.
	 *
	 * @return the timestamp
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:12
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets timestamp.
	 *
	 * @param timestamp the timestamp
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:12
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Gets is delete.
	 *
	 * @return the is delete
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:12
	 */
	public Boolean getIsDelete() {
		return isDelete;
	}

	/**
	 * Sets is delete.
	 *
	 * @param isDelete the is delete
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:12
	 */
	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTxId(), getTimestamp(), getIsDelete(), getValue());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}

		History other = (History) obj;

		return Objects.deepEquals(new Object[] { getTxId(), getTimestamp(), getIsDelete(), getValue() },
				new Object[] { other.getTxId(), other.getTimestamp(), other.getIsDelete(), other.getValue() });
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [txId=" + txId
				+ ", timestamp=" + timestamp + ", isDelete=" + isDelete + ", value=" + value + "]";
	}
}
