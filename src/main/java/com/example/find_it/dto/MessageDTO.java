package com.example.find_it.dto;

public class MessageDTO {
    private String topic;
    private int partition;
    private long offset;
    private Object value;

    public MessageDTO(String topic, int partition, long offset, Object value) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.value = value;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "topic='" + topic + '\'' +
                ", partition=" + partition +
                ", offset=" + offset +
                ", value=" + value +
                '}';
    }
}
