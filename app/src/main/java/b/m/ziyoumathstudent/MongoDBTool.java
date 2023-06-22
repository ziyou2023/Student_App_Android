package b.m.ziyoumathteacher;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class MongoDBTool {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public void MongoDBConnector(String host, int port, String dbName, String collectionName) {
        // 连接到MongoDB数据库
        mongoClient = new MongoClient(host, port);

        // 选择数据库
        database = mongoClient.getDatabase(dbName);

        // 选择集合
        collection = database.getCollection(collectionName);
    }

    public void insertDocument(Document document) {
        // 插入文档
        collection.insertOne(document);
    }

    public Document findDocument(Document query) {
        // 查询文档
        return collection.find(query).first();
    }

//    public void close() {
//        // 关闭连接
//        mongoClient.close();
//    }
}

