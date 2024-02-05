package ru.alexefremov.depositapp.depositservice.event;

import lombok.Value;

@Value
public class UserChangedEvent {
    long userId;
}
