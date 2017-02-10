package ProcessFeatures;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.types.ObjectId;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.URLEntity;

/**
 *
 * @author dgi
 */
public class TweetCleaner {

    /**
     * @param args the command line arguments
     */
    public static HashMap<String, Double> normalizeDf(HashMap<String, Integer> df, int docCounter) {

        HashMap<String, Double> idf = new HashMap();
        Set keys = df.keySet();
        Iterator<String> it = keys.iterator();
        double oldValue;
        String currKey;
        while (it.hasNext()) {
            currKey = it.next();
            oldValue = (double) df.get(currKey);
            idf.put(currKey, Math.log(((double) docCounter) / oldValue));
        }
        return idf;

    }

    /**
     * Returns the top words in a specific time Range
     * 
     * @param coll
     * @param start
     * @param end
     * @param topKwors
     * @param topKUrls
     * @param topKTweets
     * @param postingMap
     * @param idf
     * @return
     * @throws TwitterException 
     */
    public static String[] topWordsInRange(DBCollection coll, Date start, Date end,
            int topKwors, int topKUrls, int topKTweets, HashMap<Long, HashMap<String, Integer>> postingMap,
            HashMap<String, Double> idf) throws TwitterException {

        ObjectId startId = new ObjectId(start);
        ObjectId endId = new ObjectId(end);
        BasicDBObject dateQuery = new BasicDBObject("_id",
                new BasicDBObject("$gte", startId).append("$lte", endId));
        DBCursor cursor;
        cursor = coll.find(dateQuery);
        HashMap<String, Integer> postings;
        HashMap<String, Integer> urls;
        HashMap<String, Double> total = new HashMap<>();
        HashMap<String, Integer> totalUrls = new HashMap();
        HashMap<String, Integer> totalRetweet = new HashMap();
        String word = "";
        Iterator it;
        Iterator itUrl;
        Iterator itRetweet;
        double num, oldNum;
        int urlCounter;
        Long id;
        Status status;
        URLEntity[] urlArray;
        String url;
        while (cursor.hasNext()) {
            status = TwitterObjectFactory.createStatus(cursor.next().toString());
            totalRetweet.put(status.getText(), status.getRetweetCount());
            id = status.getId();
            urlArray = status.getURLEntities();
            postings = postingMap.get(id);

            it = postings.keySet().iterator();
            while (it.hasNext()) {
                word = (String) it.next();
                num = postings.get(word);
                if (total.get(word) != null) {
                    oldNum = total.get(word);
                    num = num + oldNum;
                    total.put(word, num);
                } else {
                    total.put(word, num);
                }
            }
            for (int i = 0; i < urlArray.length; ++i) {
                url = urlArray[i].getURL();
                if (totalUrls.get(url) != null) {
                    urlCounter = totalUrls.get(url);
                    urlCounter++;
                    totalUrls.put(url, urlCounter);
                } else {
                    totalUrls.put(url, 1);
                }
            }

        }
        cursor.close();

        it = total.keySet().iterator();

        while (it.hasNext()) {
            word = (String) it.next();
            oldNum = total.get(word);
            num = oldNum * idf.get(word);
            total.put(word, num);
        }
        if (total.size() < topKwors) {
            topKwors = total.size();
        }
        if (totalUrls.size() < topKUrls) {
            topKUrls = totalUrls.size();
        }
        if (totalRetweet.size() < topKTweets) {
            topKTweets = totalRetweet.size();
        }
        total = sortByDouble(total);
        totalUrls = sortByInteger(totalUrls);
        totalRetweet = sortByInteger(totalRetweet);
        it = total.keySet().iterator();
        itUrl = totalUrls.keySet().iterator();
        itRetweet = totalRetweet.keySet().iterator();
        String[] results = new String[3];
        String retweets = "<p/>" + (String) itRetweet.next();
        String words = (String) it.next();
        String temp = (String) itUrl.next();
        String urlResult = "<a href=\"" + temp + "\">" + temp + "</a>";
        for (int i = 1; i < topKwors; ++i) {

            words = words + ", " + (String) it.next();

        }
        for (int i = 1; i < topKUrls; i++) {
            temp = (String) itUrl.next();
            urlResult = urlResult + ", " + "<a href=\"" + temp + "\">" + temp + "</a>";
        }
        for (int i = 1; i < topKTweets; i++) {
            retweets = retweets + "<p/>" + (String) itRetweet.next();
        }
        results[0] = words;
        results[1] = urlResult;
        results[2] = retweets;

        return results;
    }

    /**
     * Sorts an unsorted map by value ( double ) 
     * 
     * @param unsortMap
     * @return 
     */
    private static HashMap<String, Double> sortByDouble(HashMap<String, Double> unsortMap) {

        List list = new LinkedList(unsortMap.entrySet());

        // sort list based on comparator
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // put sorted list into map again
        //LinkedHashMap make sure order in which keys were inserted
        HashMap<String, Double> sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put((String) entry.getKey(), (double) entry.getValue());
        }
        list.clear();
        return sortedMap;
    }
    
    /**
     * Sorts an unsorted map by value ( integer ) 
     * 
     * @param unsortMap
     * @return 
     */
    private static HashMap<String, Integer> sortByInteger(HashMap<String, Integer> unsortMap) {

        List list = new LinkedList(unsortMap.entrySet());

        // sort list based on comparator
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // put sorted list into map again
        //LinkedHashMap make sure order in which keys were inserted
        HashMap<String, Integer> sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put((String) entry.getKey(), (int) entry.getValue());
        }
        list.clear();
        return sortedMap;
    }

}
