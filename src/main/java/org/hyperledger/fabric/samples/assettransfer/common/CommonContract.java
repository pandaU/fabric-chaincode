package org.hyperledger.fabric.samples.assettransfer.common;

import com.google.gson.Gson;
import org.hyperledger.fabric.Logger;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.ContractRuntimeException;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.protos.peer.ChaincodeShim.QueryResponseMetadata;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.*;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.*;


/**
 * 智能合约概述，极其重要
 * <p>
 * 定义一个智能合约，合约的名字在部署时定义，因此和类名（CommonContract）并没有关系，拿到此程序后不用修改
 * <p>
 * 类中的关键词 type 代表 CouchDB（类似 MongoDB） 的一个数据库表名，一般来说一个项目对应一个智能合约，一个智能合约对应一张表
 * 因此，type 建议用 项目简码+项目特征值 进行区分，如长沙市一站式平台的 type 为 osmp_css
 * <p>不推荐将一个智能合约的数据存储到多张表中，会导致查询语句非常复杂
 * <p>
 * 名词解释
 * ContractInterface ： 智能合约定义接口，实现该接口即能将普通代码定义为智能合约
 *
 * @author XieXiongXiong
 * @Transaction 交易注解，使用在方法上，将其定义为交易方法
 * @Transaction(intent = Transaction.TYPE.EVALUATE) 代表交易查询方法
 * @Transaction(intent = Transaction.TYPE.SUBMIT)代表交易上链方法
 * stub.createCompositeKey(OBJECT_TYPE, type, key) 生成联合主键，主要做数据的逻辑隔离，数据的CRUD需要通过该主键进行
 * <p>
 * @date 2021 -07-07
 */
@Contract(name = "CommonContract", info = @Info(title = "Spring-Fabric-Gateway Common Contract", description = "The common contract with CRUD actions...", version = "1.0.0", license = @License(name = "Apache 2.0 License", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), contact = @Contact(email = "", name = "", url = "")))
@Default
public class CommonContract implements ContractInterface {
    /**
     * log
     */
    private static final Logger log = Logger.getLogger(CommonContract.class);
    /**
     * OBJECT_TYPE 组合key（联合主键）
     */
    private static final String OBJECT_TYPE = "type~key";

    private static final String TABLE = "table~type";

    private static final String TYPE_ADD_SUFFIX = "_combination@~type";

    private static final String JSON_STRING = "{";

    /**
     * Initialize
     *
     * @param context the context
     * @return Chaincode.Response string
     * @author XieXiongXiong
     * @date 2021 -07-07 10:29:12
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String queryChaincode(Context context) {
        return "commonChaincode";
    }

    /**
     * Create new object with type, key and value.
     * 在区块链上新增一条数据
     *
     * @param context the context 智能合约上下文，需要定义在每一个方法的首参数，主要能进行数据查询，上链，事件，权限等操作，业务方无需关注
     * @param type    the type 表名，合约数据存储到 CouchDB （类似 MongoDB）中的数据库表名，一般来说一个智能合约对应一张表，如用户信息智能合约存储到 user 表，不推荐一个智能合约的数据存储到多张表中，会导致查询语句非常复杂。
     * @param key     the key 数据表中记录的唯一标识，如用户信息表中的 user_id
     * @param value   the value 一条格式为 json String 的记录信息，一定要有 id 属性。 如用户信息，如 {"id":"10001","username":"张三","sex":"男","age":18}
     * @return Chaincode.Response boolean
     * @author XieXiongXiong
     * @date 2021 -07-07 10:29:12
     * <p>
     * 对应 Fabric 命令行的调用示例：peer chaincode invoke -o localhost:7050 -C mychannel -n basic -c '{"function":"create","Args":["user","10001","{\"username\":\"pandau\"}"]}'
     * 其中 -c 表示调用参数 function 指定调用智能合约中的 create 方法，
     * Args 代表参数，"user" 方法中 type 值，"10001" 方法中的 key 值， "{\"username\":\"pandau\"}" 方法中的 value 值
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean create(Context context, String type, String key, String value) {
        log.info("CommonContract.create: type=" + type + ", key=" + key + ", value=" + value);
        if (type == null || key == null) {
            throw new ContractRuntimeException("Incorrect number of arguments. Expecting 3 [type, key, value]");
        }

        ChaincodeStub stub = context.getStub();
        String compositeKey = getCompositeKey(stub, type, key);
        if (value == null) {
            return Boolean.FALSE;
        }
        //获取前一个世界状态,更新累加状态
        String addKey = getCompositeKey(stub, type + TYPE_ADD_SUFFIX, key);
        String stringState = stub.getStringState(addKey);
        log.info("old value:" + stringState);
        log.info("new value: " + value);
        String newValue = value;
        Gson gson = new Gson();
        Map map = gson.fromJson(newValue, Map.class);
        map.put("type",type + TYPE_ADD_SUFFIX);
        newValue = gson.toJson(map);
        if (Objects.nonNull(stringState) && !stringState.isEmpty()){
            Map oldMap = gson.fromJson(stringState, Map.class);
            Map newMap = gson.fromJson(value, Map.class);
            newMap.forEach(oldMap::put);
            oldMap.put("type",type + TYPE_ADD_SUFFIX);
            newValue = gson.toJson(oldMap);
        }
        stub.delState(addKey);
        stub.putStringState(addKey, newValue);
        log.info("CommonContract.create: compositeKey=" + compositeKey);
        log.info("CommonContract.create: value=" + value);
        stub.putStringState(compositeKey, value);
        String tableKey = getCompositeTableKey(stub, type);
        byte[] table = stub.getState(tableKey);
        //如果表不存在则创建表
        if (table == null || table.length < 1) {
            String tableString = "{\"tableName\":\"" + type + "\",\"type\":\"" + TABLE + "\"}";
            stub.putStringState(tableKey, tableString);
        }

        return Boolean.TRUE;
    }

    /**
     * Create new object with type, key and value.
     * 在区块链上加载一条数据，如果不存在 键为 key 值的数据则返回 null
     *
     * @param context the context 智能合约上下文，需要定义在每一个方法的首参数，主要能进行数据查询，上链，事件，权限等操作，业务方无需关注
     * @param type    the type 表名，合约数据存储到 CouchDB （类似 MongoDB）中的数据库表名，一般来说一个智能合约对应一张表，如用户信息智能合约存储到 user 表，不推荐一个智能合约的数据存储到多张表中，会导致查询语句非常复杂。
     * @param key     the key 数据表中记录的唯一标识，如用户信息表中的 user_id 如 10001
     * @return Chaincode.Response String
     * @author XieXiongXiong
     * @date 2021 -07-07 10:29:12
     * <p>
     * 对应 Fabric 命令行的调用示例：peer chaincode query -C mychannel -n basic -c '{"function":"get","Args":["user","10001"]}'
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String get(Context context, String type, String key) {
        log.info("CommonContract.get: type=" + type + ", key=" + key);
        if (type == null || key == null) {
            throw new ContractRuntimeException("Incorrect number of arguments. At least 2 [type, key, ...]");
        }

        ChaincodeStub stub = context.getStub();
        String compositeKey = getCompositeKey(stub, type, key);
        log.info("CommonContract.get: compositeKey=" + compositeKey);
        byte[] bytes = stub.getState(compositeKey);
        Map<String, Object> map = new HashMap<>(16);
        if (bytes == null || bytes.length < 1) {
            return null;
        }
        String strUtf8 = null;
        try {
            strUtf8 = new String(bytes, "utf8");
            Gson gson = new Gson();
            Object json = gson.fromJson(strUtf8, Object.class);
            map.put("id", key);
            map.put("type", type);
            map.put("values", json);
        } catch (UnsupportedEncodingException e) {
            log.error("get 编码失败");
        }
        return JsonUtil.stringify(map);
    }

    /**
     * Create new object with type, key and value.
     * 在区块链上新增一条数据更新记录，该操作会更新世界状态库的记录
     *
     * @param context the context 智能合约上下文，需要定义在每一个方法的首参数，主要能进行数据查询，上链，事件，权限等操作，业务方无需关注
     * @param type    the type 表名，合约数据存储到 CouchDB （类似 MongoDB）中的数据库表名，一般来说一个智能合约对应一张表，如用户信息智能合约存储到 user 表。
     * @param key     the key 数据表中记录的唯一标识，如用户信息表中的 user_id
     * @param value   the value 一条格式为 json String 的记录信息，一定要有 id 属性。 如用户信息，如 {"id":"10001","username":"张三","sex":"男","age":18}
     * @return Chaincode.Response boolean
     * @author XieXiongXiong
     * @date 2021 -07-07 10:29:12
     * <p>
     * 对应 Fabric 命令行的调用示例：peer chaincode invoke -o localhost:7050 -C mychannel -n basic -c '{"function":"update","Args":["user","10001","{\"username\":\"pandau\"}"]}'
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean update(Context context, String type, String key, String value) {
        log.info("CommonContract.update: type=" + type + ", key=" + key + ", value=" + value);
        if (type == null || key == null) {
            throw new ContractRuntimeException("Incorrect number of arguments. Expecting 3 [type, key, value]");
        }
        ChaincodeStub stub = context.getStub();
        String compositeKey = getCompositeKey(stub, type, key);
        byte[] resp = stub.getState(compositeKey);
        if (resp == null) {
            return Boolean.FALSE;
        }
        //获取前一个世界状态,更新累加状态
        String addKey = getCompositeKey(stub, type + TYPE_ADD_SUFFIX, key);
        String stringState = stub.getStringState(addKey);
        log.info("old value:" + stringState);
        log.info("new value: " + value);
        String newValue = value;
        Gson gson = new Gson();
        Map map = gson.fromJson(newValue, Map.class);
        map.put("type",type + TYPE_ADD_SUFFIX);
        newValue = gson.toJson(map);
        if (Objects.nonNull(stringState) && !stringState.isEmpty()){
            Map oldMap = gson.fromJson(stringState, Map.class);
            Map newMap = gson.fromJson(value, Map.class);
            newMap.forEach(oldMap::put);
            oldMap.put("type",type + TYPE_ADD_SUFFIX);
            newValue = gson.toJson(oldMap);
        }
        stub.delState(addKey);
        stub.delState(compositeKey);
        if (value == null) {
            return Boolean.FALSE;
        }
        stub.putStringState(compositeKey, value);
        stub.putStringState(addKey, newValue);
        return Boolean.TRUE;
    }

    /**
     * Create new object with type, key and value.
     * 在区块链上新增一条数据删除记录，该操作的目的是为了将一条数据标记为删除
     *
     * @param context the context 智能合约上下文，需要定义在每一个方法的首参数，主要能进行数据查询，上链，事件，权限等操作，业务方无需关注
     * @param type    the type 表名，合约数据存储到 CouchDB （类似 MongoDB）中的数据库表名，一般来说一个智能合约对应一张表，如用户信息智能合约存储到 user 表，不推荐一个智能合约的数据存储到多张表中，会导致查询语句非常复杂。
     * @param key     the key 数据表中记录的唯一标识，如用户信息表中的 user_id 如 10001
     * @return Chaincode.Response boolean
     * @author XieXiongXiong
     * @date 2021 -07-07 10:29:12
     * <p>
     * 对应 Fabric 命令行的调用示例：peer chaincode invoke -o localhost:7050 -C mychannel -n basic -c '{"function":"delete","Args":["user","10001"]}'
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean delete(Context context, String type, String key) {
        log.info("CommonContract.delete: type=" + type + ", key=" + key);
        ChaincodeStub stub = context.getStub();
        String compositeKey = getCompositeKey(stub, type, key);
        if (compositeKey == null) {
            return Boolean.FALSE;
        }
        String addDataKey = getCompositeKey(stub, type + TYPE_ADD_SUFFIX, key);
        stub.delState(compositeKey);
        stub.delState(addDataKey);
        return Boolean.TRUE;

    }


    /**
     * 按照上链 json 数据中的特殊属性进行分页查询
     * <p>
     * 其中 mongo 查询详细资料可以查看文档 https://blog.csdn.net/weixin_34037173/article/details/91809461
     *
     * @param context  the context
     * @param query    the query  ，为 mongo 语法的 json 字符串 如查询数据中性别为男性的数据，
     *                 {
     *                 "selector": {
     *                 "type":"user"
     *                 }
     *                 }
     * @param pageSize the page size ，每页的数据条数
     * @param bookmark the bookmark ，书签，一般数据的 hash 值，传空代表从第一条记录开始查询， {@link Query} {@link QueryMeta##bookmark}
     * @return {@link Query} 业务方无需关注返回结构体，SDK 已进行封装
     * @author XieXiongXiong
     * @date 2021 -07-07 10:29:13
     * <p>
     * 对应 Fabric 命令行的调用示例：peer chaincode query -C mychannel -n basic -c '{"function":"query","Args":["
     * {
     * \"selector\": {
     * \"type\":\"user\"
     * }
     * }
     * ","10",""]}'
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Query query(Context context, String query, Integer pageSize, String bookmark) {
        log.info("CommonContract.query: query=" + query + ", pageSize=" + pageSize + ", bookmark=" + bookmark);
        if (query == null) {
            throw new ContractRuntimeException(
                    "Incorrect number of arguments. At least 1 argument with query string should be set.");
        }
        ChaincodeStub stub = context.getStub();
        Query response = new Query();
        if (pageSize != null && pageSize.intValue() > -1) {
            QueryResultsIteratorWithMetadata<KeyValue> queryResultWithPagination = stub
                    .getQueryResultWithPagination(query, pageSize, bookmark);
            List<String> values = new ArrayList<String>();
            queryResultWithPagination.forEach(keyValue -> {
                String val = keyValue.getStringValue();
                log.info("CommonContract.query: keyValue=" + val);
                values.add(val);
            });
            response.setData(values.toArray(new String[values.size()]));
            QueryResponseMetadata metadata = queryResultWithPagination.getMetadata();
            if (metadata != null) {
                QueryMeta meta = new QueryMeta();
                meta.setRecordsCount(metadata.getFetchedRecordsCount());
                meta.setBookmark(metadata.getBookmark());
                response.setMeta(meta);
            }
        } else {
            QueryResultsIterator<KeyValue> queryResult = stub.getQueryResult(query);
            List<String> values = new ArrayList<String>();
            queryResult.forEach(keyValue -> {
                String val = keyValue.getStringValue();
                log.info("CommonContract.query: key=" + keyValue.getKey() + ", value=" + val);
                values.add(val);
            });
            response.setData(values.toArray(new String[values.size()]));
        }
        log.info("CommonContract.query: response=" + response);
        return response;
    }

    /**
     * Get count of object [query]
     * 按照上链 json 数据中的特殊属性进行记录数统计
     *
     * @param context the context
     * @param query   the query
     * @return the count of query
     * @author XieXiongXiong
     * @date 2021 -07-07 10:29:13
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Integer count(Context context, String query) {
        log.info("CommonContract.count: query=" + query);
        if (query == null) {
            throw new ContractRuntimeException(
                    "Incorrect number of arguments. At least 1 argument with query string should be set.");
        }
        ChaincodeStub stub = context.getStub();

        QueryResultsIterator<KeyValue> queryResult = stub.getQueryResult(query);
        if (queryResult == null) {
            return 0;
        }
        int count = 0;
        Iterator<KeyValue> it = queryResult.iterator();
        while (it.hasNext()) {
            it.next();
            count++;
        }
        log.info("CommonContract.count: " + count);
        return count;
    }

    /**
     * Check exists for query
     * 按照上链 json 数据中的特殊属性查询记录是否存在
     *
     * @param context the context
     * @param query   the query
     * @return boolean boolean
     * @author XieXiongXiong
     * @date 2021 -07-07 10:29:13
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Boolean exists(Context context, String query) {
        log.info("CommonContract.exist: query=" + query);
        if (query == null) {
            throw new ContractRuntimeException(
                    "Incorrect number of arguments. At least 1 argument with query string should be set.");
        }
        ChaincodeStub stub = context.getStub();

        QueryResultsIterator<KeyValue> queryResult = stub.getQueryResult(query);
        if (queryResult == null) {
            return Boolean.FALSE;
        }
        Iterator<KeyValue> it = queryResult.iterator();
        boolean exists = it.hasNext();
        log.info("CommonContract.exists: " + exists);
        return exists;
    }

    /**
     * Create new object with type, key and value.
     * 在区块链上加载一条数据的所有历史记录
     *
     * @param context the context 智能合约上下文，需要定义在每一个方法的首参数，主要能进行数据查询，上链，事件，权限等操作，业务方无需关注
     * @param type    the type 表名，合约数据存储到 CouchDB （类似 MongoDB）中的数据库表名，一般来说一个智能合约对应一张表，如用户信息智能合约存储到 user 表，不推荐一个智能合约的数据存储到多张表中，会导致查询语句非常复杂。
     * @param key     the key 数据表中记录的唯一标识，如用户信息表中的 user_id 如 10001
     * @return Chaincode.Response String
     * @author XieXiongXiong
     * @date 2021 -07-07 10:29:12
     * <p>
     * 对应 Fabric 命令行的调用示例：peer chaincode query -C mychannel -n basic -c '{"function":"history","Args":["user","10001"]}'
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public History[] history(Context context, String type, String key) {
        log.info("CommonContract.history: type=" + type + ", key=" + key);
        ChaincodeStub stub = context.getStub();
        String compositeKey = getCompositeKey(stub, type, key);
        List<History> histories = new ArrayList<>();
        QueryResultsIterator<KeyModification> historyIterator = stub.getHistoryForKey(compositeKey);
        if (historyIterator != null) {
            historyIterator.forEach(mod -> {
                History his = new History();
                Instant timestamp = mod.getTimestamp();
                if (timestamp != null) {
                    his.setTimestamp(timestamp.toEpochMilli());
                }
                his.setTxId(mod.getTxId());
                if (!mod.isDeleted()) {
                    his.setValue(mod.getStringValue());
                }
                his.setIsDelete(mod.isDeleted());
                histories.add(his);
            });
        }
        log.info("CommonContract.history: " + JsonUtil.stringify(histories));
        return histories.toArray(new History[histories.size()]);
    }

    /**
     * Gets composite key.
     * 新建组合key
     *
     * @param stub the stub
     * @param type the type
     * @param key  the key
     * @return the composite key
     * @author XieXiongXiong
     * @date 2021 -07-07 10:29:12
     */
    private String getCompositeKey(ChaincodeStub stub, String type, String key) {
        if (type == null || key == null) {
            throw new ContractRuntimeException("Incorrect number of arguments. At least 2 [type, key, ...]");
        }
        CompositeKey compositeKey = stub.createCompositeKey(OBJECT_TYPE, type, key);
        if (compositeKey != null) {
            return compositeKey.toString();
        }
        throw new ContractRuntimeException("Create compositeKey failed");
    }

    private String getCompositeTableKey(ChaincodeStub stub, String type) {
        if (type == null) {
            throw new ContractRuntimeException("Incorrect number of arguments. At least 1 [type, key, ...]");
        }
        CompositeKey compositeKey = stub.createCompositeKey(TABLE, type);
        if (compositeKey != null) {
            return compositeKey.toString();
        }
        throw new ContractRuntimeException("Create compositeKey failed");
    }
}
