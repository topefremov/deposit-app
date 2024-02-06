package ru.alexefremov.depositapp.depositservice.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSearchService {
    private static final String USERS_INDEX = "users";
    private final ElasticsearchOperations esOps;
    public SearchPage<UserData> search(SearchFilter searchFilter) {
        Objects.requireNonNull(searchFilter, "SearchFilter is null");
        Criteria criteria = null;

        if (StringUtils.hasText(searchFilter.getName())) {
            criteria = addCriteria(criteria, "name").startsWith(searchFilter.getName());
        }

        if (searchFilter.getDateOfBirth() != null) {
            criteria = addCriteria(criteria, "dateOfBirth").greaterThan(searchFilter.getDateOfBirth());
        }

        if (StringUtils.hasText(searchFilter.getPhone())) {
            criteria = addCriteria(criteria, "phones.number")
                    .matches(searchFilter.getPhone());
        }

        if (StringUtils.hasText(searchFilter.getEmail())) {
            criteria = addCriteria(criteria, "emails.email")
                    .matches(searchFilter.getEmail());
        }

        if (criteria == null) {
            criteria = new Criteria();
        }

        var query = new CriteriaQuery(criteria, searchFilter.getPageable());
        SearchHits<UserData> searchHits = esOps.search(query, UserData.class,
                IndexCoordinates.of(USERS_INDEX));
        return SearchHitSupport.searchPageFor(searchHits, query.getPageable());
    }

    private static Criteria addCriteria(Criteria criteria, String field) {
        if (criteria == null) {
            return new Criteria(field);
        }
        return criteria.and(field);
    }
}
