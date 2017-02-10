package ProcessFeatures;

/**
 * This class has possible emotics that we should have for sentiment analysis
 * 
 * @author moses
 */
public class FeatureOfSmiles {

    public void FeatureOfSmiles() {
    }

    public Integer[] getFeatures(String sentense) {
        String[] Senti = {
            ":-(", ":(", ":'(", ":-S", ":s", ":-|", ":|",
            "S-:", "S:", "):", ")-:", ")':", "|:", "|-:",
            ":)", ":-)", ";)", ";-)", "(Y)", "(y)", ":D",
            ":-D", "8)", "8-)", "(:", "(-:", "!", "...", "<3"};

        Integer[] resultFeature = new Integer[Senti.length];

        for (int i = 0; i < Senti.length; i++) {
            if (sentense.contains(sentense)) {
                resultFeature[i] = 1;
            } else {
                resultFeature[i] = 0;
            }
        }
        return resultFeature;
    }
}
