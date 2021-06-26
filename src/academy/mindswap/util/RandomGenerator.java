package academy.mindswap.util;

/**
 * Utility class which contains randomNumberMinMax method.
 */
public class RandomGenerator {

    /**
     * Random int generator method.
     * @param min minimum value
     * @param max maximum value
     * @return random int between chosen minimum and maximum values
     */
    public static int randomNumberMinMax(int min, int max){
        return (int)(Math.random()*(max-min +1)+min);
    }
}
