import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Playing with JDK 1.8 lambda. These examples implement own functional interfaces to prove that
 * <p>
 * 1) Lambda's are simply regular interface with single abstract method (enforce compile time check further
 * by @FunctionalInterface annotation to make sure there is only one abstract method).
 * 2) JDK provided easy way to implement elegant anonymous function representation using ARROW style (->).
 * 3) JDK comes with few useful default interfaces such as Predicate, Consumer, Producer & Function.
 * 4) JDK also has few specific variance of the above for e.g. IntPredicate that takes an integer to boolean.
 * 5) Example that showcase the inner working of Stream's reduce method.
 * 6) Example that showcase the inner working of Stream's flatMap method.
 */
public class JdkLambdaPlayground {

    public static void main(String[] str) {

        List<Val> values = Arrays.asList(new Val(1, 1), new Val(1, 2), new Val(2, 1), new Val(2, 2));

        tryOutOwnPredicateImplementation(values);

        tryOutOwnFunctionImplementation(values);

        tryOutReduce(values);

        tryOutFlatMap(values);
    }


    private static void tryOutOwnPredicateImplementation(List<Val> vals) {

        System.out.println("Before own Predicate call: " + vals);

        // Provide the predicate implementation for ExampleValPredicate in Arrow (->) style (rather than anonymous function style).
        List<Val> result = ownFilter(vals, v -> v.getV1() == 1);

        System.out.println("Own Predicate call Result: " + result);
    }


    /**
     * This is equivalent to JDK's Predicate functional interface. But its an example implementation that takes Val
     * object & determines if it can be evaluated to true.
     */
    @FunctionalInterface
    interface ExampleValPredicate {
        boolean test(Val value);
    }

    /**
     * This is equivalent to a filter method on Collection/Stream classes. But its an example implementation that takes
     * Val objects & a predicate and determines how many Val can be evaluated to true.
     */
    private static List<Val> ownFilter(List<Val> vals, ExampleValPredicate predicate) {

        List<Val> result = new ArrayList<>();

        for (Val v : vals) {
            if (predicate.test(v)) {
                result.add(v);
            }
        }

        return result;
    }


    private static void tryOutOwnFunctionImplementation(List<Val> vals) {

        System.out.println("Before own function call: " + vals);

        // Provide the implementation for ExampleValFunction in lambda style (rather than anonymous function style).
        List<Integer> result = apply(vals, v -> v.getV1() + v.getV2());

        System.out.println("Function call Result: " + result);
    }

    /**
     * This is equivalent to JDK's Function functional interface. But its an example implementation that takes Val
     * object & applies the provided logic.
     */
    @FunctionalInterface
    interface ExampleValFunction {
        int apply(Val val);
    }


    static List<Integer> apply(List<Val> input, ExampleValFunction exampleValFunction) {
        List<Integer> result = new ArrayList<>();

        for (Val v : input) {
            result.add(exampleValFunction.apply(v));
        }

        return result;
    }


    /**
     * Reduce method in Collection/Stream is a special method, it differ from scala's foldLeft or foldRight
     * because JDK authors decided to provide both sequential and parallel implementation using a single syntax.
     * Hence order of fold from left or right cannot be guaranteed. A combiner is needed and only used
     * at the end to combine the multiple reduce result during parallel reduce. Read more about this Stuart Marks answer in
     * http://stackoverflow.com/questions/24308146/why-is-a-combiner-needed-for-reduce-method-that-converts-type-in-java-8
     */
    private static void tryOutReduce(List<Val> vals) {


        // Type system for Java reduce for List<T> is 'U reduce(I, (U, T) -> U, (U, U) -> U);' Where I is Identity
        // of type U & merged with first T and further merged with next one and so on until all are reduced.
        int reduceResult = vals.stream().reduce(0, (a, b) -> a + b.getV1(), (a, b) -> {
            int c = a + b;
            System.out.println("a: " + a + " b: " + b + " c: " + c);
            return c;
        });

        System.out.println("Reduce result: " + reduceResult);

        // Find the sum of Ascii vals of A, B, C using reduce.
        int asciiSum = Arrays.asList('A', 'B', 'C').stream().parallel().reduce(0, (U, T) -> U + T.hashCode(), (U1, U2) -> U1 + U2);

        System.out.println("Reduce ascii result: " + asciiSum);
    }

    private static void tryOutFlatMap(List<Val> vals) {

        System.out.println("Before own FlatMap call: " + vals);

        List<Integer> resultMap = vals.stream().map(Val::getV1).collect(Collectors.toList());
        List<Integer> resultFlatMap = vals.stream().flatMap(e -> Stream.of(e.getV1())).collect(Collectors.toList());

        System.out.println("FlatMap call Result: " + resultMap + " resultFlatMap: " + resultFlatMap);
    }

    /**
     * A simple POJO.
     */
    private static class Val {

        private int v1;
        private int v2;

        public Val(int v1, int v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        public int getV1() {
            return v1;
        }

        public int getV2() {
            return v2;
        }

        @Override
        public String toString() {
            return "Val{" +
                    "v1=" + v1 +
                    ", v2=" + v2 +
                    '}';
        }
    }


}
