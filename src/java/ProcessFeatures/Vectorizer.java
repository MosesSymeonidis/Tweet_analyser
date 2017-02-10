/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProcessFeatures;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Moses
 */
public class Vectorizer {

    private final int[] vector;
    private final HashMap<String, Integer> termPos = new HashMap();

    public Vectorizer(HashSet<String> totalStems) {
        int counter = 0;
        vector = new int[totalStems.size()];
        Iterator<String> it = totalStems.iterator();
        while (it.hasNext()) {
            termPos.put(it.next(), counter);
            counter++;
        }
    }

    public int[] getUnigramVector(HashSet<String> stems) {
        int[] newVector = vector;
        Iterator<String> it = stems.iterator();
        int pos;
        while (it.hasNext()) {
            pos = termPos.get(it.next());
            newVector[pos] = 1;
        }
        return newVector;
    }

    public int[] mergedVector(int[] unigramVector, int emoticonsVector[]) {

        int i;
        int[] temp = new int[unigramVector.length + emoticonsVector.length];
        System.arraycopy(unigramVector, 0, temp, 0, unigramVector.length);
        System.arraycopy(emoticonsVector, 0, temp, unigramVector.length, emoticonsVector.length);

        return temp;
    }

    public int[] getEmoticonsVector(String sentense) {
        String[] Senti = {
            ":-(", ":(", ":'(", ":-S", ":s", ":-|", ":|",
            "S-:", "S:", "):", ")-:", ")':", "|:", "|-:",
            ":)", ":-)", ";)", ";-)", "(Y)", "(y)", ":D",
            ":-D", "8)", "8-)", "(:", "(-:", "!", "...", "<3", "</3"};

        int[] resultFeature = new int[Senti.length];

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
