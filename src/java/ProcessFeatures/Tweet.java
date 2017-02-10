/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProcessFeatures;

import com.mongodb.DBObject;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.URLEntity;

/**
 * Class for downloading and processing of tweets 
 * 
 * @author dgi
 */
public class Tweet {

    private Status status;
    private HashtagEntity[] hashtags;
    private URLEntity[] origUrls, origMedia;
    private final Version version = Version.LUCENE_48;
    private Analyzer analyzer;
    private StringReader sr;
    private TokenStream ts;
    private OffsetAttribute offsetAtt;
    private CharTermAttribute termAtt;
    private HashMap<String, Integer> tokens;
    private HashSet<String> stemmedSet;
    private long totalTokens = 0 ;
    private long id;
    
    /**
     * Generate a tweet object from tweet json string
     * 
     * @param rawJson
     * @throws TwitterException
     * @throws IOException 
     */
    public Tweet(String rawJson) throws TwitterException, IOException {
        this.analyzer = new StopAnalyzer(version, new File("stop.txt"));
        status = TwitterObjectFactory.createStatus(rawJson);
        hashtags = status.getHashtagEntities();
        origUrls = status.getURLEntities();
        origMedia = status.getMediaEntities();
        id = status.getId();
        tokens = findTokens();
        stemmedSet = findStemmedWords();
    }
    
    /**
     * returns id of the tweet
     * @return 
     */
    public long getId() {
        return id;
    }
    
    /**
     * Returns the status of the tweet
     * 
     * @return 
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns the tokens of the tweet
     * 
     * @return 
     */
    public HashMap<String, Integer> getTokens() throws IOException {
        return tokens;
    }

    /**
     * Returns the stemmed set
     * 
     * @return 
     */
    public HashSet<String> getStemmed() {
        return stemmedSet;
    }

    /**
     * removes urls for status
     * 
     * @param status
     * @return
     * @throws IOException 
     */
    private String removeUrls(String status) throws IOException {

        String origUrl;
        if (origUrls.length > 0) {
            for (int i = 0; i < origUrls.length; i++) {
                origUrl = origUrls[i].getURL();
                status = status.replace(origUrl, "");
            }
        }
        String media;
        if (origMedia.length > 0) {
            for (int i = 0; i < origMedia.length; i++) {
                media = origMedia[i].getURL();
                status = status.replace(media, "");
            }
        }

        return status;
    }

    /**
     * Find tokens from tweets
     * 
     * @return
     * @throws IOException 
     */
    private HashMap<String, Integer> findTokens() throws IOException {
        String text = status.getText();
        HashMap<String, Integer> docTf = new HashMap<>();
        text = removeUrls(text);
        sr = new StringReader(text);
        ts = analyzer.tokenStream("irrelevant", sr);
        //ts = new PorterStemFilter(ts);
        offsetAtt = ts.addAttribute(OffsetAttribute.class);
        termAtt = ts.addAttribute(CharTermAttribute.class);

        ts.reset();
        while (ts.incrementToken()) {

            String term = termAtt.toString();
            //System.out.println(term);
            if (docTf.get(term) != null) {
                int value = docTf.get(term);
                value++;
                docTf.put(term, value);
            } else {
                docTf.put(term, 1);
            }
            totalTokens++;
            //tokens.add(termAtt.toString());
        }
        ts.close();
        sr.close();
        return docTf;
    }
    
    /**
     * Find stemmed words 
     * @return 
     */
    private HashSet<String> findStemmedWords() {
        HashSet<String> set = new HashSet();
        Set<String> wordSet = tokens.keySet();
        Iterator<String> it = wordSet.iterator();
        String word;
        char[] charArray;
        Stemmer stemmer = new Stemmer();
        while (it.hasNext()) {
            word = it.next();
            charArray = word.toCharArray();
            stemmer.add(charArray, charArray.length);
            stemmer.stem();
            word = stemmer.toString();
            set.add(word);
        }
        return set;
    }

    /**
     * This function updates Df of the set
     * @param totaldf
     * @throws IOException 
     */
    public void updateDf(HashMap<String, Integer> totaldf) throws IOException {

        Set<String> tokenKeySet = tokens.keySet();
        Iterator<String> tokenIt = tokenKeySet.iterator();
        String token;
        int docValue, oldValue;
        while (tokenIt.hasNext()) {
            token = tokenIt.next();
            docValue = tokens.get(token);
            if (totaldf.get(token) != null) {
                oldValue = totaldf.get(token);
                oldValue = oldValue + docValue;
                totaldf.put(token, oldValue);
            } else {
                totaldf.put(token, docValue);
            }
        }
    }
    
    /**
     * updates total set of steams
     * @param totalSet 
     */
    public void updateStems(HashSet<String> totalSet) {
        Iterator<String> it = stemmedSet.iterator();
        while (it.hasNext()) {
            totalSet.add(it.next());
        }
    }
}
