package com.nnt.test_worker.work.datatypes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Data {

    /**
     * An empty Data object with no elements.
     */
    public static final Data EMPTY = new Data.Builder().build();

    @SuppressWarnings("WeakerAccess") /* synthetic access */
            Map<String, Object> mValues;

    Data() {    // stub required for room
    }

    public Data(Data other) {
        mValues = new HashMap<>(other.mValues);
    }

    Data(Map<String, ?> values) {
        mValues = new HashMap<>(values);
    }

    /**
     * Gets the boolean value for the given key.
     *
     * @param key          The key for the argument
     * @param defaultValue The default value to return if the key is not found
     * @return The value specified by the key if it exists; the default value otherwise
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = mValues.get(key);
        if (value instanceof Boolean) {
            return (boolean) value;
        } else {
            return defaultValue;
        }
    }

    /**
     * Gets the boolean array value for the given key.
     *
     * @param key The key for the argument
     * @return The value specified by the key if it exists; {@code null} otherwise
     */
    public boolean[] getBooleanArray(String key) {
        Object value = mValues.get(key);
        if (value instanceof Boolean[]) {
            Boolean[] array = (Boolean[]) value;
            boolean[] returnArray = new boolean[array.length];
            for (int i = 0; i < array.length; ++i) {
                returnArray[i] = array[i];
            }
            return returnArray;
        } else {
            return null;
        }
    }


    /**
     * Gets the integer value for the given key.
     *
     * @param key          The key for the argument
     * @param defaultValue The default value to return if the key is not found
     * @return The value specified by the key if it exists; the default value otherwise
     */
    public int getInt(String key, int defaultValue) {
        Object value = mValues.get(key);
        if (value instanceof Integer) {
            return (int) value;
        } else {
            return defaultValue;
        }
    }

    /**
     * Gets the integer array value for the given key.
     *
     * @param key The key for the argument
     * @return The value specified by the key if it exists; {@code null} otherwise
     */
    public int[] getIntArray(String key) {
        Object value = mValues.get(key);
        if (value instanceof Integer[]) {
            Integer[] array = (Integer[]) value;
            int[] returnArray = new int[array.length];
            for (int i = 0; i < array.length; ++i) {
                returnArray[i] = array[i];
            }
            return returnArray;
        } else {
            return null;
        }
    }

    /**
     * Gets the long value for the given key.
     *
     * @param key          The key for the argument
     * @param defaultValue The default value to return if the key is not found
     * @return The value specified by the key if it exists; the default value otherwise
     */
    public long getLong(String key, long defaultValue) {
        Object value = mValues.get(key);
        if (value instanceof Long) {
            return (long) value;
        } else {
            return defaultValue;
        }
    }

    /**
     * Gets the long array value for the given key.
     *
     * @param key The key for the argument
     * @return The value specified by the key if it exists; {@code null} otherwise
     */
    public long[] getLongArray(String key) {
        Object value = mValues.get(key);
        if (value instanceof Long[]) {
            Long[] array = (Long[]) value;
            long[] returnArray = new long[array.length];
            for (int i = 0; i < array.length; ++i) {
                returnArray[i] = array[i];
            }
            return returnArray;
        } else {
            return null;
        }
    }

    /**
     * Gets the float value for the given key.
     *
     * @param key          The key for the argument
     * @param defaultValue The default value to return if the key is not found
     * @return The value specified by the key if it exists; the default value otherwise
     */
    public float getFloat(String key, float defaultValue) {
        Object value = mValues.get(key);
        if (value instanceof Float) {
            return (float) value;
        } else {
            return defaultValue;
        }
    }

    /**
     * Gets the float array value for the given key.
     *
     * @param key The key for the argument
     * @return The value specified by the key if it exists; {@code null} otherwise
     */
    public float[] getFloatArray(String key) {
        Object value = mValues.get(key);
        if (value instanceof Float[]) {
            Float[] array = (Float[]) value;
            float[] returnArray = new float[array.length];
            for (int i = 0; i < array.length; ++i) {
                returnArray[i] = array[i];
            }
            return returnArray;
        } else {
            return null;
        }
    }

    /**
     * Gets the double value for the given key.
     *
     * @param key          The key for the argument
     * @param defaultValue The default value to return if the key is not found
     * @return The value specified by the key if it exists; the default value otherwise
     */
    public double getDouble(String key, double defaultValue) {
        Object value = mValues.get(key);
        if (value instanceof Double) {
            return (double) value;
        } else {
            return defaultValue;
        }
    }

    /**
     * Gets the double array value for the given key.
     *
     * @param key The key for the argument
     * @return The value specified by the key if it exists; {@code null} otherwise
     */
    public double[] getDoubleArray(String key) {
        Object value = mValues.get(key);
        if (value instanceof Double[]) {
            Double[] array = (Double[]) value;
            double[] returnArray = new double[array.length];
            for (int i = 0; i < array.length; ++i) {
                returnArray[i] = array[i];
            }
            return returnArray;
        } else {
            return null;
        }
    }

    /**
     * Gets the String value for the given key.
     *
     * @param key The key for the argument
     * @return The value specified by the key if it exists; the default value otherwise
     */
    public String getString(String key) {
        Object value = mValues.get(key);
        if (value instanceof String) {
            return (String) value;
        } else {
            return null;
        }
    }

    /**
     * Gets the String array value for the given key.
     *
     * @param key The key for the argument
     * @return The value specified by the key if it exists; {@code null} otherwise
     */
    public String[] getStringArray(String key) {
        Object value = mValues.get(key);
        if (value instanceof String[]) {
            return (String[]) value;
        } else {
            return null;
        }
    }

    /**
     * Gets all the values in this Data object.
     *
     * @return A {@link Map} of key-value pairs for this object; this Map is unmodifiable and should
     * be used for reads only.
     */
    public Map<String, Object> getKeyValueMap() {
        return Collections.unmodifiableMap(mValues);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Data other = (Data) o;
        return mValues.equals(other.mValues);
    }

    @Override
    public int hashCode() {
        return 31 * mValues.hashCode();
    }

    @SuppressWarnings("WeakerAccess") /* synthetic access */
    static Boolean[] convertPrimitiveBooleanArray(boolean[] value) {
        Boolean[] returnValue = new Boolean[value.length];
        for (int i = 0; i < value.length; ++i) {
            returnValue[i] = value[i];
        }
        return returnValue;
    }

    @SuppressWarnings("WeakerAccess") /* synthetic access */
    static Integer[] convertPrimitiveIntArray(int[] value) {
        Integer[] returnValue = new Integer[value.length];
        for (int i = 0; i < value.length; ++i) {
            returnValue[i] = value[i];
        }
        return returnValue;
    }

    @SuppressWarnings("WeakerAccess") /* synthetic access */
    static Long[] convertPrimitiveLongArray(long[] value) {
        Long[] returnValue = new Long[value.length];
        for (int i = 0; i < value.length; ++i) {
            returnValue[i] = value[i];
        }
        return returnValue;
    }

    @SuppressWarnings("WeakerAccess") /* synthetic access */
    static Float[] convertPrimitiveFloatArray(float[] value) {
        Float[] returnValue = new Float[value.length];
        for (int i = 0; i < value.length; ++i) {
            returnValue[i] = value[i];
        }
        return returnValue;
    }

    @SuppressWarnings("WeakerAccess") /* synthetic access */
    static Double[] convertPrimitiveDoubleArray(double[] value) {
        Double[] returnValue = new Double[value.length];
        for (int i = 0; i < value.length; ++i) {
            returnValue[i] = value[i];
        }
        return returnValue;
    }

    /**
     * A builder for {@link Data} objects.
     */
    public static final class Builder {

        private final Map<String, Object> mValues = new HashMap<>();

        /**
         * Puts a boolean into the arguments.
         *
         * @param key   The key for this argument
         * @param value The value for this argument
         * @return The {@link Builder}
         */
        public Builder putBoolean(String key, boolean value) {
            mValues.put(key, value);
            return this;
        }

        /**
         * Puts a boolean array into the arguments.
         *
         * @param key   The key for this argument
         * @param value The value for this argument
         * @return The {@link Builder}
         */
        public Builder putBooleanArray(String key, boolean[] value) {
            mValues.put(key, convertPrimitiveBooleanArray(value));
            return this;
        }

        /**
         * Puts an integer into the arguments.
         *
         * @param key   The key for this argument
         * @param value The value for this argument
         * @return The {@link Builder}
         */
        public Builder putInt(String key, int value) {
            mValues.put(key, value);
            return this;
        }

        /**
         * Puts an integer array into the arguments.
         *
         * @param key   The key for this argument
         * @param value The value for this argument
         * @return The {@link Builder}
         */
        public Builder putIntArray(String key, int[] value) {
            mValues.put(key, convertPrimitiveIntArray(value));
            return this;
        }

        /**
         * Puts a long into the arguments.
         *
         * @param key   The key for this argument
         * @param value The value for this argument
         * @return The {@link Builder}
         */
        public Builder putLong(String key, long value) {
            mValues.put(key, value);
            return this;
        }

        /**
         * Puts a long array into the arguments.
         *
         * @param key   The key for this argument
         * @param value The value for this argument
         * @return The {@link Builder}
         */
        public Builder putLongArray(String key, long[] value) {
            mValues.put(key, convertPrimitiveLongArray(value));
            return this;
        }

        /**
         * Puts a float into the arguments.
         *
         * @param key   The key for this argument
         * @param value The value for this argument
         * @return The {@link Builder}
         */
        public Builder putFloat(String key, float value) {
            mValues.put(key, value);
            return this;
        }

        /**
         * Puts a float array into the arguments.
         *
         * @param key   The key for this argument
         * @param value The value for this argument
         * @return The {@link Builder}
         */
        public Builder putFloatArray(String key, float[] value) {
            mValues.put(key, convertPrimitiveFloatArray(value));
            return this;
        }

        /**
         * Puts a double into the arguments.
         *
         * @param key   The key for this argument
         * @param value The value for this argument
         * @return The {@link Builder}
         */
        public Builder putDouble(String key, double value) {
            mValues.put(key, value);
            return this;
        }

        /**
         * Puts a double array into the arguments.
         *
         * @param key   The key for this argument
         * @param value The value for this argument
         * @return The {@link Builder}
         */
        public Builder putDoubleArray(String key, double[] value) {
            mValues.put(key, convertPrimitiveDoubleArray(value));
            return this;
        }

        /**
         * Puts a String into the arguments.
         *
         * @param key   The key for this argument
         * @param value The value for this argument
         * @return The {@link Builder}
         */
        public Builder putString(String key, String value) {
            mValues.put(key, value);
            return this;
        }

        /**
         * Puts a String array into the arguments.
         *
         * @param key   The key for this argument
         * @param value The value for this argument
         * @return The {@link Builder}
         */
        public Builder putStringArray(String key, String[] value) {
            mValues.put(key, value);
            return this;
        }

        /**
         * Puts all input key-value pairs from a {@link Data} into the Builder.
         * <p>
         * Valid value types are: Boolean, Integer, Long, Float, Double, String, and their array
         * versions.  Invalid types will throw an {@link IllegalArgumentException}.
         *
         * @param data {@link Data} containing key-value pairs to add
         * @return The {@link Builder}
         */
        public Builder putAll(Data data) {
            putAll(data.mValues);
            return this;
        }

        /**
         * Puts all input key-value pairs from a {@link Map} into the Builder.
         * <p>
         * Valid value types are: Boolean, Integer, Long, Float, Double, String, and their array
         * versions.  Invalid types will throw an {@link IllegalArgumentException}.
         *
         * @param values A {@link Map} of key-value pairs to add
         * @return The {@link Builder}
         */
        public Builder putAll(Map<String, Object> values) {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                put(key, value);
            }
            return this;
        }

        /**
         * Puts an input key-value pair into the Builder. Valid types are: Boolean, Integer,
         * Long, Float, Double, String, and array versions of each of those types.
         * Invalid types throw an {@link IllegalArgumentException}.
         *
         * @param key   A {@link String} key to add
         * @param value A nullable {@link Object} value to add of the valid types
         * @return The {@link Builder}
         * @hide
         */
        public Builder put(String key, Object value) {
            if (value == null) {
                mValues.put(key, null);
            } else {
                Class valueType = value.getClass();
                if (valueType == Boolean.class
                        || valueType == Integer.class
                        || valueType == Long.class
                        || valueType == Float.class
                        || valueType == Double.class
                        || valueType == String.class
                        || valueType == Boolean[].class
                        || valueType == Integer[].class
                        || valueType == Long[].class
                        || valueType == Float[].class
                        || valueType == Double[].class
                        || valueType == String[].class) {
                    mValues.put(key, value);
                } else if (valueType == boolean[].class) {
                    mValues.put(key, convertPrimitiveBooleanArray((boolean[]) value));
                } else if (valueType == int[].class) {
                    mValues.put(key, convertPrimitiveIntArray((int[]) value));
                } else if (valueType == long[].class) {
                    mValues.put(key, convertPrimitiveLongArray((long[]) value));
                } else if (valueType == float[].class) {
                    mValues.put(key, convertPrimitiveFloatArray((float[]) value));
                } else if (valueType == double[].class) {
                    mValues.put(key, convertPrimitiveDoubleArray((double[]) value));
                } else {
                    throw new IllegalArgumentException(
                            String.format("Key %s has invalid type %s", key, valueType));
                }
            }
            return this;
        }

        /**
         * Builds a {@link Data} object.
         *
         * @return The {@link Data} object containing all key-value pairs specified by this
         * {@link Builder}.
         */
        public Data build() {
            Data data = new Data(mValues);
            return data;
        }
    }
}
