package ru.alexefremov.depositapp.depositservice.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ru.alexefremov.depositapp.depositservice.search.UserData;

public interface UserDataRepository extends ElasticsearchRepository<UserData, Long> {
}
