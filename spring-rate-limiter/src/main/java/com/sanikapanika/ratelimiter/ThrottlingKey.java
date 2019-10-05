package com.sanikapanika.ratelimiter;

import com.sanikapanika.ratelimiter.cache.Cache;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Class holding method execution context
 * Used as a key in {@link Cache}
 */
public class ThrottlingKey {

    private final Method method;
    private final int limit;
    private final ThrottlingType type;
    private final Long timeUnit;
    private final String evaluatedValue;

    private ThrottlingKey(Method method, int limit, ThrottlingType type, Long timeUnit, String evaluatedValue) {
        this.method = method;
        this.limit = limit;
        this.type = type;
        this.timeUnit = timeUnit;
        this.evaluatedValue = evaluatedValue;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Method getMethod() {
        return method;
    }

    public int getLimit() {
        return limit;
    }

    public ThrottlingType getType() {
        return type;
    }

    public Long getTimeUnit() {
        return timeUnit;
    }

    public String getEvaluatedValue() {
        return evaluatedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThrottlingKey)) return false;

        ThrottlingKey that = (ThrottlingKey) o;

        if (limit != that.limit) return false;
        if (!method.equals(that.method)) return false;
        if (type != that.type) return false;
        if (!timeUnit.equals(that.timeUnit)) return false;
        return Objects.equals(evaluatedValue, that.evaluatedValue);
    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + limit;
        result = 31 * result + type.hashCode();
        result = 31 * result + timeUnit.hashCode();
        result = 31 * result + (evaluatedValue != null ? evaluatedValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ThrottlingKey{" +
                "method=" + method +
                ", limit=" + limit +
                ", type=" + type +
                ", timeUnit=" + timeUnit +
                ", evaluatedValue='" + evaluatedValue + '\'' +
                '}';
    }

    public static class Builder {
        private Method method;
        private int limit;
        private ThrottlingType type;
        private Long timeUnit;
        private String evaluatedValue;

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Builder annotation(Throttling throttling) {
            this.limit = throttling.limit();
            this.type = throttling.type();
            this.timeUnit = throttling.timeUnit();
            return this;
        }

        public Builder evaluatedValue(String evaluatedValue) {
            this.evaluatedValue = evaluatedValue;
            return this;
        }

        public ThrottlingKey build() {
            return new ThrottlingKey(method, limit, type, timeUnit, evaluatedValue);
        }
    }
}
