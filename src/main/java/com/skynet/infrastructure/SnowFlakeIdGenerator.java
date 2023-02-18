package com.skynet.infrastructure;


public class SnowFlakeIdGenerator {

    private long workerId;
    private long dataCenterId;
    private long sequence = 0;

    // the start timestamp in milliseconds (2018-01-01 00:00:00)
    private long startEpoch = 1514736000000L;

    private long workerIdBits = 5L;
    private long dataCenterIdBits = 5L;
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);
    private long sequenceBits = 12L;

    private long workerIdShift = sequenceBits;
    private long dataCenterIdShift = sequenceBits + workerIdBits;
    private long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
    private long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;

    public SnowFlakeIdGenerator(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("Data center Id can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    private long blockUtilNextMillis() {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // the sequence is overflowed
                timestamp = blockUtilNextMillis();
            }
        }

        lastTimestamp = timestamp;
        return ((timestamp - startEpoch) << timestampLeftShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }
}
