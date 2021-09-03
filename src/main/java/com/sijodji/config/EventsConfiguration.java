package com.sijodji.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsConfiguration {

    private Map<String, EventConfiguration> events;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventConfiguration {
        @Getter(AccessLevel.NONE)
        private Boolean enable = Boolean.TRUE;
        private String consumer;
        private String producer;

        public Boolean isEnable() {
            return this.enable;
        }
    }
}
