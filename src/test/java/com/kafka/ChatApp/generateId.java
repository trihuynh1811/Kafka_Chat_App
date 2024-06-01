package com.kafka.ChatApp;

import org.junit.jupiter.api.Test;

import java.util.Random;

public class generateId {

    @Test
    public void generateMessageId(){
        Random random = new Random();

        // Generate a sequence of 15 random digits
        StringBuilder sequence = new StringBuilder();
        int messageIndicator = 3;

        for (int i = 0; i < 15; i++) {
            int randomDigit = random.nextInt(10); // Generates random number between 0 and 9 (inclusive)
            sequence.append(randomDigit);
        }

        String finalMessageId = Integer.toString(messageIndicator) + sequence.toString();
        System.out.println(Long.parseLong(finalMessageId));
    }
}
