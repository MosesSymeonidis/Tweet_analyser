/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProcessFeatures;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.bson.types.ObjectId;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

/**
 *
 * This class has the main methods of sentiment analysis and tweet peaks finder
 *   
 * @author Moses
 */
public class PeakFinding_SentiAnalysis {

    static final long ONE_MINUTE_IN_MILLIS = 60000;

    /**
     * returns cursor from start date to end date
     * 
     * @param coll
     * @param start
     * @param end
     * @return 
     */
    public DBCursor getCursorInRange(DBCollection coll, Date start, Date end) {

        ObjectId startId = new ObjectId(start);
        ObjectId endId = new ObjectId(end);
        BasicDBObject dateQuery = new BasicDBObject("_id",
                new BasicDBObject("$gte", startId).append("$lt", endId));
        return coll.find(dateQuery);
    }
    
    /**
     * returns an array of strings with generated json representation of sentiment
     * analysis data
     * 
     * @param start
     * @param end
     * @param Duration
     * @param classifier
     * @return
     * @throws UnknownHostException
     * @throws TwitterException
     * @throws Exception 
     */
    public String[] sentiStream(Date start, Date end, Integer Duration,
            SentimentClassifier classifier) throws UnknownHostException, TwitterException, Exception {
        String country = "[";
        String[] result = new String[3];
        long BuckSize = ONE_MINUTE_IN_MILLIS * Duration;
        MongoClient mongoClient = new MongoClient("localhost");
        DB db = mongoClient.getDB("mydb");
        DBObject obj;
        final DBCollection collection = db.getCollection("dummyColl");
        Status status;
        int sumNeg, sumPos, sumNeu, sumNegAll, sumPosAll, sumNeuAll;
        String stringTimeline = "[['Time', 'Positive', 'Negative','Neutral'],";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
        sumNegAll = 0;
        sumPosAll = 0;
        sumNeuAll = 0;
        for (long time = start.getTime(); time <= end.getTime(); time += BuckSize) {
            sumNeg = 0;
            sumPos = 0;
            sumNeu = 0;
            DBCursor temp = getCursorInRange(collection, new Date(time), new Date(time + BuckSize));

            while (temp.hasNext()) {
                obj = temp.next();

                status = TwitterObjectFactory.createStatus(obj.toString());

                String sentiTest = classifier.classify(status.getText());
                if ("neutral".equals(sentiTest)) {
                    sumNeu++;
                    if (status.getGeoLocation() != null) {
                        country = country + "['Neutral'," + status.getGeoLocation().getLatitude() + "," + status.getGeoLocation().getLongitude() + ",'yellow'],";
                    }
                } else if ("positive".equals(sentiTest)) {
                    if (status.getGeoLocation() != null) {
                        country = country + "['Positive'," + status.getGeoLocation().getLatitude() + "," + status.getGeoLocation().getLongitude() + ",'blue'],";
                    }
                    sumPos++;
                } else {
                    if (status.getGeoLocation() != null) {
                        country = country + "['Negative'," + status.getGeoLocation().getLatitude() + "," + status.getGeoLocation().getLongitude() + ",'red'],";
                    }
                    sumNeg++;
                }

            }
            sumNegAll += sumNeg;
            sumPosAll += sumPos;
            sumNeuAll += sumNeu;
            stringTimeline = stringTimeline + "['" + formatter.format(new Date(time)) + "',"
                    + sumPos + "," + sumNeg + "," + sumNeu + "],";

        }
        stringTimeline = stringTimeline + "]";
        result[0] = stringTimeline;

        result[2] = country + "]";
        result[1] = "[['Sentiment','number of Tweets'],"
                + "['Positive'," + sumPosAll + "],"
                + "['Negative'," + sumNegAll + "],"
                + "['Neutral'," + sumNeuAll + "]]";

        return result;
    }

    /**
     * 
     * Returns an array of strings which have the peak of tweets
     * 
     * @param start
     * @param end
     * @param topWords
     * @param topURLs
     * @param topRetweets
     * @param Duration
     * @param idf
     * @param postingMap
     * @return
     * @throws UnknownHostException
     * @throws IOException
     * @throws TwitterException 
     */
    public String[] stringOfPeaks(Date start, Date end, Integer topWords, Integer topURLs, Integer topRetweets, Integer Duration, HashMap<String, Double> idf,
            HashMap<Long, HashMap<String, Integer>> postingMap) throws UnknownHostException, IOException, TwitterException {
        MongoClient mongoClient = new MongoClient("localhost");

        int i = 0;
        long BuckSize = ONE_MINUTE_IN_MILLIS * Duration;
        DB db = mongoClient.getDB("mydb");
        ArrayList<Date> buckets = new ArrayList();
        ArrayList<Integer> timeLine = new ArrayList();
        final DBCollection collection = db.getCollection("dummyColl");
        boolean t = true;
        for (long time = start.getTime(); time <= end.getTime(); time += BuckSize) {
            buckets.add(new Date(time));
            int k = getCursorInRange(collection, new Date(time), new Date(time + BuckSize)).count();

            timeLine.add(k);

        }
        ArrayList<String> peakAreas = new <String>ArrayList();
        for (i = 0; i < buckets.size(); i++) {
            peakAreas.add("null");
        }
        ArrayList<String> peakAreas2 = new <String>ArrayList();
        for (i = 0; i < buckets.size(); i++) {
            peakAreas2.add("false,");
        }

        Integer l = 1;
        Integer[] timeLineArray = timeLine.toArray(new Integer[0]);
        ArrayList<String> peaks = PeakFinder.peakFinder(timeLineArray, 0.125, 5, 3);

        String resultString = "";

        for (String key : peaks) {
            String[] tempAreas = key.split(" ");
            if (Integer.parseInt(tempAreas[1]) >= buckets.size()) {
                tempAreas[1] = Integer.toString(buckets.size() - 1);
            }
            String[] results = TweetCleaner.topWordsInRange(collection, buckets.get(Integer.parseInt(tempAreas[0])), buckets.get(Integer.parseInt(tempAreas[1])),
                    topWords, topURLs, topRetweets, postingMap, idf);

            int max = 0, index = -1;
            for (i = Integer.parseInt(tempAreas[0]); i <= Integer.parseInt(tempAreas[1]); i++) {
                if (i < peakAreas.size()) {
                    peakAreas2.set(i, "true,");
                    if (max < timeLineArray[i]) {
                        index = i;
                        max = timeLineArray[i];
                    }
                }

            }
            peakAreas.set(index, "'Peak" + Integer.toString(l) + "'");
            resultString += "<p><div class=\"alert alert-info\"><h4><span class=\"label label-primary\">Peak" + Integer.toString(l) + "</span></h4><span class=\"label label-danger\">Words:</span> " + results[0] + " <p><span class=\"label label-info\">URLs:</span> " + results[1] + "<p><span class=\"label label-success\">Most Retweeted tweets:</span> " + results[2] + "</div>";
            l++;
        }

        String sum = "[";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
        for (i = 0; i < buckets.size(); i++) {

            sum = sum + "['" + formatter.format(buckets.get(i)) + "'," + timeLine.get(i) + ",";

            sum = sum + peakAreas2.get(i);

            sum = sum + peakAreas.get(i) + "],";
        }
        sum = sum + "]";
        String[] fin = new String[2];
        fin[0] = sum;
        fin[1] = resultString;
        return fin;
    }
}
