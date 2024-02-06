package ru.alexefremov.depositapp.depositservice.search;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Document(indexName = "users")
public class UserData {
    @Id
    private long id;
    @Field(type = FieldType.Text)
    private String name;
    @Field(type = FieldType.Date)
    private LocalDate dateOfBirth;
    private Account account;
    private List<Phone> phones;
    private List<Email> emails;

    @Value
    @Builder
    @Jacksonized
    public static class Phone {
        long id;
        @Field(type = FieldType.Keyword)
        String number;
    }

    @Value
    @Builder
    @Jacksonized
    public static class Email {
        long id;
        @Field(type = FieldType.Keyword)
        String email;
    }

    @Value
    @Builder
    @Jacksonized
    public static class Account {
        long id;
        @Field(type = FieldType.Float)
        BigDecimal balance;
    }
}
