package ProcessFeatures;

import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.LMClassifier;
import com.aliasi.util.AbstractExternalizable;
import java.io.File;
import java.io.IOException;

/**
 *
 * The sentiment classifier
 * 
 * @author Moses
 */
public class SentimentClassifier {

    String[] categories;
    LMClassifier classifier;

    public SentimentClassifier() {
        try {
            classifier = (LMClassifier) AbstractExternalizable.readObject(new File("classifier.dat"));
            categories
                    = classifier.categories();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String classify(String text) {
        ConditionalClassification classification = classifier.classify(text);
        return classification.bestCategory();
    }
}
