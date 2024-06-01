package com.kafka.ChatApp.Entity.CustomGenerator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.util.Random;

public class ConversationIdGenerator implements IdentifierGenerator {
    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        try {
            Random random = new Random();

            // Generate a sequence of 15 random digits
            StringBuilder sequence = new StringBuilder();
            int messageIndicator = 2;

            for (int i = 0; i < 15; i++) {
                int randomDigit = random.nextInt(10); // Generates random number between 0 and 9 (inclusive)
                sequence.append(randomDigit);
            }

            String finalMessageId = Integer.toString(messageIndicator) + sequence.toString();
            System.out.println(Long.parseLong(finalMessageId));
            return Long.parseLong(finalMessageId);
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
