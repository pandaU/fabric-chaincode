package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * The type Wallet chaincode.
 *
 * @author XieXiongXiong
 * @date 2021 -07-05
 */
//@Contract(
//        name = "wallet")
//@Default
public class WalletOperate implements ContractInterface {
    private final Genson genson = new Genson();

    private enum WalletOperateErrors {
        Wallet_NOT_FOUND,
        Wallet_ALREADY_EXISTS,
        PAGE_SIZE_NULL_OR_ZERO
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Wallet CreateWallet(final Context ctx, final String walletId, final Long money, final Long accountId) {
        ChaincodeStub stub = ctx.getStub();
        System.out.println(stub.getStringArgs());
        if (WalletExists(ctx, walletId)) {
            String errorMessage = String.format("Wallet %s already exists", walletId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, WalletOperateErrors.Wallet_ALREADY_EXISTS.toString());
        }

        Wallet wallet = new Wallet(walletId, money, accountId);
        String walletJSON = genson.serialize(wallet);
        System.out.println(walletJSON);
        stub.putStringState(walletId, walletJSON);
        System.out.println(wallet);
        return wallet;
    }

    /**
     * Retrieves an Wallet with the specified ID from the ledger.
     *
     * @param ctx the transaction context
     * @param walletId the ID of the Wallet
     * @return the Wallet found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Wallet ReadWallet(final Context ctx, final String walletId) {
        ChaincodeStub stub = ctx.getStub();
        String walletJSON = stub.getStringState(walletId);

        if (walletJSON == null || walletJSON.isEmpty()) {
            String errorMessage = String.format("Wallet %s does not exist", walletId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, WalletOperateErrors.Wallet_NOT_FOUND.toString());
        }

        Wallet wallet = genson.deserialize(walletJSON, Wallet.class);
        return wallet;
    }

    /**
     * Updates the properties of an Wallet on the ledger.
     *
     * @param ctx the transaction context
     * @param walletId the ID of the Wallet being updated
     * @return the transferred Wallet
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Wallet UpdateWallet(final Context ctx, final String walletId, final Long money, final Long accountId) {
        ChaincodeStub stub = ctx.getStub();

        if (!WalletExists(ctx, walletId)) {
            String errorMessage = String.format("Wallet %s does not exist", walletId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, WalletOperateErrors.Wallet_NOT_FOUND.toString());
        }

        Wallet newWallet = new Wallet(walletId, money,accountId);
        String newWalletJSON = genson.serialize(newWallet);
        stub.putStringState(walletId, newWalletJSON);

        return newWallet;
    }

    /**
     * Deletes Wallet on the ledger.
     *
     * @param ctx the transaction context
     * @param walletId the ID of the Wallet being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteWallet(final Context ctx, final String walletId) {
        ChaincodeStub stub = ctx.getStub();

        if (!WalletExists(ctx, walletId)) {
            String errorMessage = String.format("wallet %s does not exist", walletId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage,WalletOperateErrors.Wallet_NOT_FOUND.toString());
        }

        stub.delState(walletId);
    }

    /**
     * Checks the existence of the Wallet on the ledger
     *
     * @param ctx the transaction context
     * @param walletId the ID of the Wallet
     * @return boolean indicating the existence of the Wallet
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean WalletExists(final Context ctx, final String walletId) {
        ChaincodeStub stub = ctx.getStub();
        String wallet = stub.getStringState(walletId);
        return (wallet != null && !wallet.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetWalletsByPage(final Context ctx, final Long accountId, final int pageSize, final String bookMark) {
        ChaincodeStub stub = ctx.getStub();
        if (pageSize == 0 ){
            throw  new ChaincodeException(WalletOperateErrors.PAGE_SIZE_NULL_OR_ZERO.toString());
        }
        List<Wallet> queryResults = new ArrayList<Wallet>();
        String query = "{\n" +
                "   \"selector\": {}\n" +
                "}";
        if (accountId != null && accountId >  0){
            query = query.replace("{}","{\"accountId\":"+accountId+"}");
        }
        System.out.println(query);
        // To retrieve all Wallets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'Wallet0', endKey = 'Wallet9' ,
        // then getStateByRange will retrieve Wallet with keys between Wallet0 (inclusive) and Wallet9 (exclusive) in lexical order.
        QueryResultsIteratorWithMetadata<KeyValue> results = stub.getQueryResultWithPagination(query, pageSize, bookMark);
        for (KeyValue result: results) {
            Wallet wallet = genson.deserialize(result.getStringValue(), Wallet.class);
            queryResults.add(wallet);
            System.out.println(wallet.toString());
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", queryResults);
        map.put("last", queryResults.size() > 0 ? results.getMetadata().getBookmark() : "");
        System.out.println(map);
        final String response = genson.serialize(map);

        return response;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Wallet[] GetWalletHistoryByKey(final Context ctx,final String walletId) {
        ChaincodeStub stub = ctx.getStub();
        List<Wallet> queryResults = new ArrayList<Wallet>();
        QueryResultsIterator<KeyModification> history = stub.getHistoryForKey(walletId);

        for (KeyModification  recode: history) {
            Wallet wallet = genson.deserialize(recode.getStringValue(),Wallet.class);
            queryResults.add(wallet);
            System.out.println(wallet.toString());
        }
        return queryResults.toArray(new Wallet[0]);
    }
}
