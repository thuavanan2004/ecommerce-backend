package com.devdynamo.repositories;

import com.devdynamo.dtos.response.PageResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.devdynamo.utils.AppConst.SORT_BY;

@Repository
@Slf4j
public class SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String LIKE_FORMAT = "%%%s%%";

    public PageResponse<?> getProducts(int pageNo, int pageSize, String search, String sortBy){
        log.info("Search products with keyword={}", search);
        StringBuilder sql = new StringBuilder("SELECT new com.devdynamo.dtos.response.ProductResponseDTO(p.id, p.name, p.description, p.price, p.discount, p.stockQuantity, p.category.id, p.category.name, p.imageUrl, p.createdAt, p.updatedAt) FROM ProductEntity p  WHERE 1 = 1 ");
        if(StringUtils.hasLength(search)){
            sql.append(" AND lower(p.name) like lower(:name)");
            sql.append(" OR lower(p.description) like lower(:description)");
        }

        if(StringUtils.hasLength(sortBy)){
            Pattern pattern = Pattern.compile(SORT_BY);
            Matcher matcher = pattern.matcher(sortBy);

            if(matcher.find()){
                sql.append(String.format(" ORDER BY p.%s %s", matcher.group(1), matcher.group(3)));
            }
        }

        Query selectQuery = entityManager.createQuery(sql.toString());

        if(StringUtils.hasLength(search)){
            selectQuery.setParameter("name", String.format(LIKE_FORMAT, search));
            selectQuery.setParameter("description", String.format(LIKE_FORMAT, search));
        }
        selectQuery.setFirstResult(pageNo * pageSize);
        selectQuery.setMaxResults(pageSize);
        List<?> products = selectQuery.getResultList();

        StringBuilder sqlCountQuery = new StringBuilder("SELECT COUNT(*) FROM ProductEntity p WHERE 1 = 1");

        if(StringUtils.hasLength(search)){
            sqlCountQuery.append(" AND lower(p.name) like lower(:name)");
            sqlCountQuery.append(" OR lower(p.description) like lower(:description)");
        }

        Query countQuery = entityManager.createQuery(sqlCountQuery.toString());

        if(StringUtils.hasLength(search)){
            countQuery.setParameter("name", String.format(LIKE_FORMAT, search));
            countQuery.setParameter("description", String.format(LIKE_FORMAT, search));
            countQuery.getSingleResult();
        }

        Long totalElements = (Long) countQuery.getSingleResult();
        log.info("totalElements={}", totalElements);

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<?> page = new PageImpl<>(products, pageable, totalElements);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(products)
                .build();
    }
}
