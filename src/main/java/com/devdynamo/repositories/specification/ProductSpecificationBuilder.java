package com.devdynamo.repositories.specification;

import com.devdynamo.entities.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static com.devdynamo.repositories.specification.SearchOperation.*;

public class ProductSpecificationBuilder {
    private final List<SpecSearchCriteria> params;

    public ProductSpecificationBuilder() {
        this.params = new ArrayList<>();
    }

    public ProductSpecificationBuilder with( String key, String operation, String value, String prefix, String suffix){
        return with(null, key, operation, value, prefix, suffix);
    }

    public ProductSpecificationBuilder with(String orPredicate, String key, String operation, String value, String prefix, String suffix){
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));
        if(searchOperation != null){
            if(searchOperation == EQUALITY){
                boolean startWithAsterisk = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX);
                boolean endWithAsterisk = suffix != null && suffix.contains(ZERO_OR_MORE_REGEX);
                if(startWithAsterisk && endWithAsterisk){
                    searchOperation = LIKE;
                }else if(startWithAsterisk){
                    searchOperation = STARTS_WITH;
                }else if(endWithAsterisk){
                    searchOperation = ENDS_WITH;
                }
            }
            params.add(new SpecSearchCriteria(key, searchOperation, value, orPredicate));
        }
        return this;
    }

    public Specification<ProductEntity> build(){
        if(params.isEmpty()) return null;

        Specification<ProductEntity> result = new ProductSpecification(params.get(0));
        for (int i = 0; i < params.size(); i ++){
            result = params.get(i).isOrPredicate() ?
                    Specification.where(result).or(new ProductSpecification(params.get(i))) : Specification.where(result).and(new ProductSpecification(params.get(i)));
        }
        return result;
    }
}
