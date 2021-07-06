package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

/**
 * <p>
 * The type Wallet.
 *
 * @author XieXiongXiong
 * @date 2021 -07-05
 */
@DataType()
public class Wallet {
    /**
     * Wallet id
     */
    @Property()
    private final String walletId;
    /**
     * Money
     */
    @Property()
    private final Long money;
    /**
     * Account id
     */
    @Property()
    private final Long accountId;

    public String getWalletId() {
        return walletId;
    }

    public Long getMoney() {
        return money;
    }

    public Long getAccountId() {
        return accountId;
    }

    /**
     * Wallet
     *
     * @param walletId  wallet id
     * @param money     money
     * @param accountId account id
     */
    public Wallet(@JsonProperty("walletId")String walletId, @JsonProperty("money")Long money,  @JsonProperty("accountId")Long accountId) {
        this.walletId = walletId;
        this.money = money;
        this.accountId = accountId;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Wallet other = (Wallet) obj;

        return Objects.deepEquals(
                new String[] {getWalletId()},
                new String[] {other.getWalletId()})
                &&
                Objects.deepEquals(
                        new Long[] {getAccountId(), getMoney()},
                        new Long[] {other.getAccountId(), other.getMoney()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWalletId(), getAccountId(), getMoney());
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [accountId=" + getAccountId() + ", money="
                +  getMoney() + ", walletId=" + getWalletId() + "]";
    }
}
