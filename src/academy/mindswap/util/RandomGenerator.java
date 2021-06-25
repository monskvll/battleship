package academy.mindswap.util;

public class RandomGenerator {

    public static int randomNumber(int number) {

        return (int)(Math.random() * number);
    }

    public static int randomNumberMinMax(int min, int max){
        return (int)(Math.random()*(max-min +1)+min);
    }
}
