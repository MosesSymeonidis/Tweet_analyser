
package ProcessFeatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * This is the class of PeakFinder algorithm 
 * 
 * @author Administrator
 */
public class PeakFinder {

    /**
     * returns the mean of C
     * 
     * @param c
     * @return 
     */
    static double getMean(Integer[] c)
    {
        double sum = 0.0;
        for (double a : c) {
            sum += a;
        }
        return sum / c.length;
    }

    /**
     * returns the varience of C
     * 
     * @param c
     * @return 
     */
    static double getVariance(Integer[] c)
    {
        double mean = getMean(c);
        double temp = 0;
        for (double a : c) {
            temp += (mean - a) * (mean - a);
        }
        return temp / c.length;
    }

    /**
     * helping sort class
     * help us to sort the peaks based on number of tweets
     */
    static class ValueComparator implements Comparator<String> {
        
        Map<String, Integer> base;

        ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        @Override
        public int compare(String a, String b) {
            Integer x = base.get(a);
            Integer y = base.get(b);
            if (x.equals(y)) {
                return a.compareTo(b);
            }
            return y.compareTo(x);
        }
    }

    /**
     * main method of the algorithm 
     * @param c
     * @param a
     * @param k
     * @param t
     * @return 
     */
    public static ArrayList<String> peakFinder(Integer[] c, double a, int k, int t) {
        
        double mean = c[0];
        double meandev = getVariance(Arrays.copyOfRange(c, 0, k));
        int start;
        ArrayList<String> windows = new ArrayList();
        int end;
        
        for (int i = 2; i < c.length; i++) {
            if ((Math.abs(c[i] - mean) / meandev) > t && c[i] > c[i - 1]) {
                start = i - 1;

                while (i < c.length && c[i] > c[i - 1]) {
                    double temp[] = update(mean, meandev, c[i], a);
                    mean = temp[0];
                    meandev = temp[1];
                    i++;
                }
                end = i;
                while (i < c.length && c[i] > c[start]) {

                    if (Math.abs(c[i] - mean) / meandev > t && c[i] > c[i - 1]) {
                        end = --i;
                        break;
                    } else {
                        double temp[] = update(mean, meandev, c[i], a);
                        mean = temp[0];
                        meandev = temp[1];
                        end = i++;
                    }
                }
                windows.add(Integer.toString(start) + " " + Integer.toString(end));
            } else {
                double temp[] = update(mean, meandev, c[i], a);
                mean = temp[0];
                meandev = temp[1];

            }
        }
        return windows;
    }

    /**
     * update the mean value
     * 
     * @param oldmean
     * @param oldmeandev
     * @param updatevalue
     * @param a
     * @return 
     */
    public static double[] update(double oldmean, double oldmeandev, int updatevalue, double a) {
        double diff = Math.abs(oldmean - updatevalue);
        double[] newValues = new double[2];
        newValues[0] = (a * updatevalue + (1 - a) * oldmean);
        newValues[1] = (a * diff + (1 - a) * oldmeandev);

        return newValues;
    }

}
