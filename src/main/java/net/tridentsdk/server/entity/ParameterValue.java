package net.tridentsdk.server.entity;

/**
 * Immutable parameter type and object value for dynamic constructor resolvation
 *
 * @author The TridentSDK Team
 * @param <T> the type for the parameter
 */
public class ParameterValue<T> {
    private final Class<T> c;
    private final T value;

    private ParameterValue(Class<T> c, T value) {
        this.c = c;
        this.value = value;
    }

    /**
     * Creates a new parameter value
     *
     * @param c the class type
     * @param value the value of the parameter
     * @param <T> the type
     * @return the new parameter value
     */
    public static <T> ParameterValue from(Class<T> c, T value) {
        return new ParameterValue<>(c, value);
    }

    /**
     * The class type for this parameter
     *
     * @return the parameter class type
     */
    public Class<T> clazz() {
        return this.c;
    }

    /**
     * The argument to be passed in for the parameter
     *
     * @return the value passed for the parameter
     */
    public T value() {
        return this.value;
    }
}
