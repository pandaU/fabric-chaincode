package org.hyperledger.fabric.samples.assettransfer.common;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Arrays;
import java.util.Objects;


@DataType
public class Query {

	/**
	 * Data
	 */
	@Property
	private String[] data;

	/**
	 * Meta
	 */
	@Property
	private QueryMeta meta;

	/**
	 * Get data string [ ].
	 *
	 * @return the string [ ]
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:11
	 */
	public String[] getData() {
		return data;
	}

	/**
	 * Sets data.
	 *
	 * @param data the data
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:11
	 */
	public void setData(String[] data) {
		this.data = data;
	}

	/**
	 * Gets meta.
	 *
	 * @return the meta
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:11
	 */
	public QueryMeta getMeta() {
		return meta;
	}

	/**
	 * Sets meta.
	 *
	 * @param meta the meta
	 * @author XieXiongXiong
	 * @date 2021 -07-07 10:29:11
	 */
	public void setMeta(QueryMeta meta) {
		this.meta = meta;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getData(), getMeta());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}

		Query other = (Query) obj;

		return Objects.deepEquals(new Object[] { getData(), getMeta() },
				new Object[] { other.getData(), other.getMeta() });
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [data="
				+ Arrays.toString(data) + ", meta=" + meta + "]";
	}
}
