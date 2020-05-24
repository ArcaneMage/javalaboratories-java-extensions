package org.javalaboratories.core.handlers;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class HandlersTest {
    private Logger logger = LoggerFactory.getLogger(HandlersTest.class);

    @Test
    public void testConsumer_Pass() {
        Consumer<String> consumer = Handlers.consumer(this::doSomethingMethod);
        assertNotNull(consumer);
        consumer.accept("testConsumer_Pass - doSomethingMethod");
    }

    @Test
    public void testBiConsumer_Pass() {
        BiConsumer<String,String> biConsumer = Handlers.biConsumer((a, b) -> doSomethingMethod(a));
        assertNotNull(biConsumer);
        biConsumer.accept("testBiConsumer_Pass - doSomethingMethod","");
    }

    @Test
    public void testUnaryOperator_Pass() {
        UnaryOperator<String> unaryOperator = Handlers.unaryOperator(s -> {doSomethingMethod(s); return "255";});
        assertNotNull(unaryOperator);

        assertEquals("255",unaryOperator.apply("testUnaryOperator_Pass - doSomethingMethod"));
    }

    @Test
    public void testBinaryOperator_Pass() {
        BinaryOperator<String> binaryOperator = Handlers.binaryOperator((a, b) -> {doSomethingMethod(a,Integer.parseInt(b)); return "128";});
        assertNotNull(binaryOperator);

        assertEquals("128",binaryOperator.apply("testUnaryOperator_Pass - doSomethingMethod","100"));
    }

    @Test
    public void testBiFunction_Pass() {
        BiFunction<String,Integer,Integer> biFunction = Handlers.biFunction(this::doSomethingMethod);
        assertNotNull(biFunction);

        assertEquals(128,biFunction.apply("testBiFunction_Pass - doSomethingMethod",100));
    }

    @Test
    public void testPredicate_Pass() {
        Predicate<String> predicate = Handlers.predicate(s -> {doSomethingMethod(s); return true; });
        assertNotNull(predicate);

        assertTrue(predicate.test("testPredicate_Pass - doSomethingMethod"));
    }

    @Test
    public void testBiPredicate_Pass() {
        BiPredicate<String,Integer> biPredicate = Handlers.biPredicate((a, b) -> {doSomethingMethod(a,b); return true; });
        assertNotNull(biPredicate);

        assertTrue(biPredicate.test("testPredicate_Pass - doSomethingMethod",100));
    }

    @Test
    public void testFunction_Pass() {
        Function<String,Integer> function = Handlers.function(this::doSomethingMethod);
        assertNotNull(function);

        assertEquals(255,function.apply("testFunction_Pass - doSomethingMethod"));
    }

    @Test
    public void testCallable_Pass() {
        Callable<Integer> callable = Handlers.callable(() -> doSomethingMethod("testCallable_Pass - doSomethingMethod"));
        assertNotNull(callable);

        try {
            assertEquals(255, callable.call());
        } catch (Exception e) {
            // Unhandled
        }
    }

    @Test
    public void testRunnable_Pass() {
        Runnable runnable = Handlers.runnable(() -> doSomethingMethod("testRunnable_Pass - doSomethingMethod"));
        runnable.run();
        assertNotNull(runnable);
    }

    @Test
    public void testConsumer_Fail() {
        Consumer<String> consumer = Handlers.consumer(this::doSomethingMethodThrowsException);
        assertThrows(RuntimeException.class, () -> consumer.accept("testConsumer_Fail - doSomethingMethodThrowsException"));

        Consumer<String> consumer2 = Handlers.consumer(this::doSomethingMethodThrowsIOException);
        assertThrows(RuntimeException.class, () -> consumer2.accept("testConsumer_Fail - doSomethingMethodThrowsIOException"));

        Consumer<String> consumer3 = Handlers.consumer(this::doSomethingMethodThrowsRuntimeException);
        assertThrows(RuntimeException.class, () -> consumer3.accept("testConsumer_Fail - doSomethingMethodThrowsRuntimeException"));

        Consumer<String> consumer4 = Handlers.consumer(this::doSomethingMethodThrowsError);
        assertThrows(RuntimeException.class, () -> consumer4.accept("testConsumer_Fail - doSomethingMethodThrowsError"));
    }

    @Test
    public void testBiConsumer_Fail() {
        BiConsumer<String,String> biConsumer = Handlers.biConsumer((a, b) -> doSomethingMethodThrowsException(a));
        assertThrows(RuntimeException.class, () -> biConsumer.accept("testBiConsumer_Fail - doSomethingMethodThrowsException",""));

        BiConsumer<String,String> biConsumer2 = Handlers.biConsumer((a, b) -> doSomethingMethodThrowsIOException(a));
        assertThrows(RuntimeException.class, () -> biConsumer2.accept("testBiConsumer_Fail - doSomethingMethodThrowsIOException",""));

        BiConsumer<String,String> biConsumer3 = Handlers.biConsumer((a, b) -> doSomethingMethodThrowsRuntimeException(a));
        assertThrows(RuntimeException.class, () -> biConsumer3.accept("testBiConsumer_Fail - doSomethingMethodThrowsRuntimeException",""));

        BiConsumer<String,String> biConsumer4 = Handlers.biConsumer((a, b) -> doSomethingMethodThrowsError(a));
        assertThrows(RuntimeException.class, () -> biConsumer4.accept("testBiConsumer_Fail - doSomethingMethodThrowsError",""));
    }

    @Test
    public void testFunction_Fail() {
        Function<String,Integer> function = Handlers.function(a -> {doSomethingMethodThrowsException(a); return 1;});
        assertThrows(RuntimeException.class, () -> function.apply("testFunction_Fail - doSomethingMethodThrowsException"));

        Function<String,Integer> function2 = Handlers.function(a -> {doSomethingMethodThrowsIOException(a); return 1;});
        assertThrows(RuntimeException.class, () -> function2.apply("testFunction_Fail - doSomethingMethodThrowsIOException"));

        Function<String,Integer> function3 = Handlers.function(a -> {doSomethingMethodThrowsRuntimeException(a); return 1;});
        assertThrows(RuntimeException.class, () -> function3.apply("testFunction_Fail - doSomethingMethodThrowsRuntimeException"));

        Function<String,Integer> function4 = Handlers.function(a -> {doSomethingMethodThrowsError(a); return 1;});
        assertThrows(RuntimeException.class, () -> function4.apply("testFunction_Fail - doSomethingMethodThrowsError"));
    }

    @Test
    public void testBiFunction_Fail() {
        BiFunction<String,Integer,Integer> biFunction = Handlers.biFunction((a, b) -> {doSomethingMethodThrowsException(a); return 1;});
        assertThrows(RuntimeException.class, () -> biFunction.apply("testBiFunction_Fail - doSomethingMethodThrowsException",0));

        BiFunction<String,Integer,Integer> biFunction2 = Handlers.biFunction((a, b) -> {doSomethingMethodThrowsIOException(a); return 1;});
        assertThrows(RuntimeException.class, () -> biFunction2.apply("testBiFunction_Fail - doSomethingMethodThrowsIOException",0));

        BiFunction<String,Integer,Integer> biFunction3 = Handlers.biFunction((a, b) -> {doSomethingMethodThrowsRuntimeException(a); return 1;});
        assertThrows(RuntimeException.class, () -> biFunction3.apply("testBiFunction_Fail - doSomethingMethodThrowsRuntimeException",0));

        BiFunction<String,Integer,Integer> biFunction4 = Handlers.biFunction((a, b) -> {doSomethingMethodThrowsError(a); return 1;});
        assertThrows(RuntimeException.class, () -> biFunction4.apply("testBiFunction_Fail - doSomethingMethodThrowsError",0));
    }

    @Test
    public void testPredicate_Fail() {
        Predicate<String> predicate = Handlers.predicate(a -> {doSomethingMethodThrowsException(a); return true;});
        assertThrows(RuntimeException.class, () -> predicate.test("testPredicate_Fail - doSomethingMethodThrowsException"));

        Predicate<String> predicate2 = Handlers.predicate(a -> {doSomethingMethodThrowsIOException(a); return true;});
        assertThrows(RuntimeException.class, () -> predicate2.test("testPredicate_Fail - doSomethingMethodThrowsIOException"));

        Predicate<String> predicate3 = Handlers.predicate(a -> {doSomethingMethodThrowsRuntimeException(a); return true;});
        assertThrows(RuntimeException.class, () -> predicate3.test("testPredicate_Fail - doSomethingMethodThrowsRuntimeException"));

        Predicate<String> predicate4 = Handlers.predicate(a -> {doSomethingMethodThrowsError(a); return true;});
        assertThrows(RuntimeException.class, () -> predicate4.test("testPredicate_Fail - doSomethingMethodThrowsError"));
    }

    @Test
    public void testBiPredicate_Fail() {
        BiPredicate<String,String> predicate = Handlers.biPredicate((a, b) -> {doSomethingMethodThrowsException(a); return true;});
        assertThrows(RuntimeException.class, () -> predicate.test("testBiPredicate_Fail - doSomethingMethodThrowsException",""));

        BiPredicate<String,String> predicate2 = Handlers.biPredicate((a, b) -> {doSomethingMethodThrowsIOException(a); return true;});
        assertThrows(RuntimeException.class, () -> predicate2.test("testBiPredicate_Fail - doSomethingMethodThrowsIOException",""));

        BiPredicate<String,String> predicate3 = Handlers.biPredicate((a, b) -> {doSomethingMethodThrowsRuntimeException(a); return true;});
        assertThrows(RuntimeException.class, () -> predicate3.test("testBiPredicate_Fail - doSomethingMethodThrowsRuntimeException",""));

        BiPredicate<String,String> predicate4 = Handlers.biPredicate((a, b) -> {doSomethingMethodThrowsError(a); return true;});
        assertThrows(RuntimeException.class, () -> predicate4.test("testBiPredicate_Fail - doSomethingMethodThrowsError",""));
    }

    @Test
    public void testUnaryOperation_Fail() {
        UnaryOperator<String> unaryOperator = Handlers.unaryOperator(a -> {doSomethingMethodThrowsException(a); return null;});
        assertThrows(RuntimeException.class, () -> unaryOperator.apply("testUnaryOperation_Fail - doSomethingMethodThrowsException"));

        UnaryOperator<String> unaryOperator2  = Handlers.unaryOperator(a -> {doSomethingMethodThrowsIOException(a); return null;});
        assertThrows(RuntimeException.class, () -> unaryOperator2.apply("testUnaryOperation_Fail - doSomethingMethodThrowsIOException"));

        UnaryOperator<String> unaryOperator3  = Handlers.unaryOperator(a -> {doSomethingMethodThrowsRuntimeException(a); return null;});
        assertThrows(RuntimeException.class, () -> unaryOperator3.apply("testUnaryOperation_Fail - doSomethingMethodThrowsRuntimeException"));

        UnaryOperator<String> unaryOperator4  = Handlers.unaryOperator(a -> {doSomethingMethodThrowsError(a); return null;});
        assertThrows(RuntimeException.class, () -> unaryOperator4.apply("testUnaryOperation_Fail - doSomethingMethodThrowsError"));
    }

    @Test
    public void testBinaryOperation_Fail() {
        BinaryOperator<String> binaryOperator = Handlers.binaryOperator((a, b) -> {doSomethingMethodThrowsException(a); return null;});
        assertThrows(RuntimeException.class, () -> binaryOperator.apply("testBinaryOperation_Fail - doSomethingMethodThrowsException",""));

        BinaryOperator<String> binaryOperator2  = Handlers.binaryOperator((a, b) -> {doSomethingMethodThrowsIOException(a); return null;});
        assertThrows(RuntimeException.class, () -> binaryOperator2.apply("testBinaryOperation_Fail - doSomethingMethodThrowsIOException",""));

        BinaryOperator<String> binaryOperator3  = Handlers.binaryOperator((a, b) -> {doSomethingMethodThrowsRuntimeException(a); return null;});
        assertThrows(RuntimeException.class, () -> binaryOperator3.apply("testBinaryOperation_Fail - doSomethingMethodThrowsRuntimeException",""));

        BinaryOperator<String> binaryOperator4  = Handlers.binaryOperator((a, b) -> {doSomethingMethodThrowsError(a); return null;});
        assertThrows(RuntimeException.class, () -> binaryOperator4.apply("testBinaryOperation_Fail - doSomethingMethodThrowsError",""));
    }

    @Test
    public void testCallable_Fail() {
        Callable<Integer> callable = Handlers.callable(() -> { doSomethingMethodThrowsException("testCallable_Fail - doSomethingMethodThrowsException"); return 128;});
        assertThrows(RuntimeException.class, callable::call);

        Callable<Integer> callable2 = Handlers.callable(() -> { doSomethingMethodThrowsIOException("testCallable_Fail - doSomethingMethodThrowsIOException"); return 128;});
        assertThrows(RuntimeException.class, callable2::call);

        Callable<Integer> callable3 = Handlers.callable(() -> { doSomethingMethodThrowsRuntimeException("testCallable_Fail - doSomethingMethodThrowsRuntimeException"); return 128;});
        assertThrows(RuntimeException.class, callable3::call);

        Callable<Integer> callable4 = Handlers.callable(() -> { doSomethingMethodThrowsError("testCallable_Fail - doSomethingMethodThrowsError"); return 128;});
        assertThrows(RuntimeException.class, callable4::call);
    }

    @Test
    public void testRunnable_Fail() {
        Runnable runtime = Handlers.runnable(() -> doSomethingMethodThrowsException("testRunnable_Fail - doSomethingMethodThrowsException"));
        assertThrows(RuntimeException.class, runtime::run);

        Runnable runtime2 = Handlers.runnable(() -> doSomethingMethodThrowsIOException("testRunnable_Fail - doSomethingMethodThrowsIOException"));
        assertThrows(RuntimeException.class, runtime2::run);

        Runnable runtime3 = Handlers.runnable(() -> doSomethingMethodThrowsRuntimeException("testRunnable_Fail - doSomethingMethodThrowsRuntimeException"));
        assertThrows(RuntimeException.class, runtime3::run);

        Runnable runtime4 = Handlers.runnable(() -> doSomethingMethodThrowsError("testRunnable_Fail - doSomethingMethodThrowsError"));
        assertThrows(RuntimeException.class, runtime4::run);
    }


    private int doSomethingMethod(String s) {
        logger.info("doSomethingMethod: Received message value: {}",s);
        return 255;
    }

    private int doSomethingMethod(String s,int value) {
        logger.info("doSomethingMethod: Received message values: {}, {}",s,value);
        return 128;
    }

    private void doSomethingMethodThrowsException(String s) throws Exception {
        logger.info("doSomethingMethodThrowsException: Received message value: {}",s);
       throw new Exception("Throwing an Exception object");
    }

    private void doSomethingMethodThrowsIOException(String s) throws IOException {
        logger.info("doSomethingMethodThrowsIOException: Received message value: {}",s);
        throw new IOException ("Throwing an IO Exception object");
    }

    private void doSomethingMethodThrowsRuntimeException(String s){
        logger.info("doSomethingMethodThrowsRuntimeException: Received message value: {}",s);
        throw new RuntimeException("Throwing a Runtime Exception object");
    }

    private void doSomethingMethodThrowsError(String s) {
        logger.info("doSomethingMethodThrowsError: Received message value: {}",s);
        throw new Error("Throwing an Error object");
    }
}
