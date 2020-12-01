package org.javalaboratories.core.concurrency;

/**
 * An object that implements this interface is said to be invocable by the
 * {@link Promises} factory.
 * <p>
 * {@link Invocable#invokeAction(PrimaryAction)} method is called after the
 * {@link Promise} object is created and therefore plays a role in the the
 * life cycle of a {@code Promise} object.
 * <p>
 * A {@code Promise} object's task will not be invoked unless this interface
 * is implemented.
 *
 * @param <T> Type of value returned from the {@link PrimaryAction}
 * @see AsyncUndertaking
 * @see Promises
 */
public interface Invocable<T> {
    /**
     * This is the initial action of the {@code Promise} object. The contract
     * is to execute the action asynchronously and to return {@code true} for
     * successful invocation. The method is called immediately after the
     * construction of the the {@link Promise} object.
     *
     * @param action the primary action to run asynchronously.
     * @return true for successful invocation.
     */
    boolean invokeAction(PrimaryAction<T> action);
}
