package com.example.pact.consumer;

import com.fasterxml.jackson.annotation.JsonCreator;

public interface MixIns {

    abstract class NameMixIn {

        @JsonCreator
        NameMixIn(String value) {

        }
    }
}
