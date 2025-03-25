package com.devdynamo.repositories.specification;

import com.devdynamo.entities.OrderEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static com.devdynamo.repositories.specification.SearchOperation.*;

public final class OrderSpecificationsBuilder {
    public final List<SpecSearchCriteria> params;

    public OrderSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    public OrderSpecificationsBuilder with(final String key, final String operation, final Object value, final String prefix, final String suffix) {
        return with(null, key, operation, value, prefix, suffix);
    }

    public OrderSpecificationsBuilder with(final String orPredicate, final String key, final String operation, final Object value, final String prefix, final String suffix){
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));
        if(searchOperation != null){
            if(searchOperation == EQUALITY){
                final boolean startWithAsterisk = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX);
                final boolean endWithAsterisk = suffix != null && suffix.contains(ZERO_OR_MORE_REGEX);
                if(startWithAsterisk && endWithAsterisk){
                    searchOperation = CONTAINS;
                } else if (startWithAsterisk) {
                    searchOperation = ENDS_WITH;
                } else if(endWithAsterisk){
                    searchOperation = STARTS_WITH;
                }
            }

            params.add(new SpecSearchCriteria(key, searchOperation, value, orPredicate));
        }

        return this;
    }

    public Specification<OrderEntity> build(){
        if(params.isEmpty()) return null;

        Specification<OrderEntity> result = new OrderSpecification(params.get(0));

        for (int i = 0; i < params.size(); i++) {
            result = params.get(i).isOrPredicate() ?
                    Specification.where(result).or(new OrderSpecification(params.get(i))) : Specification.where(result).and(new OrderSpecification(params.get(i)));
        }
        return result;
    }

    public OrderSpecificationsBuilder with(OrderSpecification spec) {
        params.add(spec.getCriteria());
        return this;
    }

    public OrderSpecificationsBuilder with(SpecSearchCriteria criteria) {
        params.add(criteria);
        return this;
    }
}
