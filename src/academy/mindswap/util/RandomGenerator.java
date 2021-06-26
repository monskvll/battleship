package academy.mindswap.util;

public class RandomGenerator {

    public static int randomNumberMinMax(int min, int max){
        return (int)(Math.random()*(max-min +1)+min);
    }
}
