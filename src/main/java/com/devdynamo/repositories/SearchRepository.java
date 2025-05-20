package com.devdynamo.repositories;

import com.devdynamo.dtos.response.OrderResponseDTO;
import com.devdynamo.dtos.response.PageResponse;
import com.devdynamo.dtos.response.ProductResponseDTO;
import com.devdynamo.entities.CategoryEntity;
import com.devdynamo.entities.OrderEntity;
import com.devdynamo.entities.ProductEntity;
import com.devdynamo.entities.UserEntity;
import com.devdynamo.mappers.OrderMapper;
import com.devdynamo.mappers.ProductMapper;
import com.devdynamo.repositories.specification.SpecSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.devdynamo.utils.AppConst.SEARCH_SPEC_OPERATOR;
import static com.devdynamo.utils.AppConst.SORT_BY;

@Repository
@Slf4j
@RequiredArgsConstructor
public class SearchRepository {
    private final OrderMapper orderMapper;

    private final ProductMapper productMapper;

    @PersistenceContext
    private EntityManager entityManager;

    private static final String LIKE_FORMAT = "%%%s%%";

    public PageResponse<?> getProducts(int pageNo, int pageSize, String search, String sortBy){
        log.info("Search products with keyword={}", search);
        StringBuilder sql = new StringBuilder("SELECT p FROM ProductEntity p  WHERE 1 = 1 ");
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
        List<ProductEntity> entities = selectQuery.getResultList();

        List<ProductResponseDTO> products = entities.stream().map(productMapper::toDTO).toList();

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

    public PageResponse<?> searchOrderByCriteriaWithJoin(Pageable pageable, String[] order, String[] user) {
        log.info("-------------- searchUserByCriteriaWithJoin --------------");

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderEntity> query = builder.createQuery(OrderEntity.class);
        Root<OrderEntity> orderRoot = query.from(OrderEntity.class);
        Join<OrderEntity, UserEntity> userRoot = orderRoot.join("user");

        List<Predicate> orderPreList = new ArrayList<>();
        Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);
        for(String o : order){
            Matcher matcher = pattern.matcher(o);
            if(matcher.find()){
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                orderPreList.add(toOrderPredicate(orderRoot, builder, criteria));
            }
        }

        List<Predicate> userPreList = new ArrayList<>();
        for(String u : user){
            Matcher matcher = pattern.matcher(u);
            if(matcher.find()){
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                userPreList.add(toUserPredicate(userRoot, builder, criteria));
            }
        }

        Predicate orderPre = builder.or(orderPreList.toArray(new Predicate[0]));
        Predicate userPre = builder.or(userPreList.toArray(new Predicate[0]));
        Predicate finalPre = builder.and(orderPre, userPre);

        query.where(finalPre);

        List<OrderEntity> orders =  entityManager.createQuery(query).setFirstResult(pageable.getPageNumber()).setMaxResults(pageable.getPageSize()).getResultList();
        List<OrderResponseDTO> list = orders.stream().map(orderMapper::toDTOWithItems).toList();

        long count = countOrderJoinUser(order, user);
        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(count)
                .items(list)
                .build();
    }

    public Predicate toOrderPredicate(Root<OrderEntity> root, CriteriaBuilder criteriaBuilder, SpecSearchCriteria criteria) {
        return switch (criteria.getOperation()){
            case EQUALITY -> criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }

    public Predicate toUserPredicate(Join<OrderEntity, UserEntity> root, CriteriaBuilder criteriaBuilder, SpecSearchCriteria criteria) {
        return switch (criteria.getOperation()){
            case EQUALITY -> criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }

    public long countOrderJoinUser(String[] order, String[] user){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<OrderEntity> orderRoot = query.from(OrderEntity.class);
        Join<OrderEntity, UserEntity> userRoot = orderRoot.join("user");

        List<Predicate> orderPreList = new ArrayList<>();
        Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);
        for(String o : order){
            Matcher matcher = pattern.matcher(o);
            if(matcher.find()){
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                orderPreList.add(toOrderPredicate(orderRoot, builder, criteria));
            }
        }

        List<Predicate> userPreList = new ArrayList<>();
        for(String u : user){
            Matcher matcher = pattern.matcher(u);
            if(matcher.find()){
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                userPreList.add(toUserPredicate(userRoot, builder, criteria));
            }
        }

        Predicate orderPre = builder.or(orderPreList.toArray(new Predicate[0]));
        Predicate userPre = builder.or(userPreList.toArray(new Predicate[0]));
        Predicate finalPre = builder.and(orderPre, userPre);

        query.select(builder.count(orderRoot));
        query.where(finalPre);

        return entityManager.createQuery(query).getSingleResult();
    }

    public PageResponse<?> searchProductByCriteriaWithJoin(Pageable pageable, String[] products, String[] categories){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> query = builder.createQuery(ProductEntity.class);
        Root<ProductEntity> productRoot = query.from(ProductEntity.class);
        Join<ProductEntity, CategoryEntity> categoryRoot = productRoot.join("category");

        List<Predicate> productPreList = new ArrayList<>();
        Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);
        for (String p : products){
            Matcher matcher = pattern.matcher(p);
            if(matcher.find()){
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                productPreList.add(toProductPredicate(productRoot, builder, criteria));
            }
        }

        List<Predicate> categoryPreList = new ArrayList<>();
        for (String c : categories){
            Matcher matcher = pattern.matcher(c);
            if(matcher.find()){
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                categoryPreList.add(toCategoryPredicate(categoryRoot, builder, criteria));
            }
        }

        Predicate finalPre = null;

        if (!productPreList.isEmpty() && !categoryPreList.isEmpty()) {
            Predicate productPre = builder.or(productPreList.toArray(new Predicate[0]));
            Predicate categoryPre = builder.or(categoryPreList.toArray(new Predicate[0]));
            finalPre = builder.and(productPre, categoryPre);
        } else if (!productPreList.isEmpty()) {
            finalPre = builder.or(productPreList.toArray(new Predicate[0]));
        } else if (!categoryPreList.isEmpty()) {
            finalPre = builder.or(categoryPreList.toArray(new Predicate[0]));
        }
        if (finalPre != null) {
            query.where(finalPre);
        }


        query.where(finalPre);
        List<ProductEntity> list = entityManager.createQuery(query).setFirstResult(pageable.getPageNumber()).setMaxResults(pageable.getPageSize()).getResultList();
        List<ProductResponseDTO> results = list.stream().map(productMapper::toDTO).toList();

        long count = countProductJoinCategory(products, categories);
        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(count)
                .items(results)
                .build();
    }

    public Predicate toProductPredicate(Root<ProductEntity> root, CriteriaBuilder criteriaBuilder, SpecSearchCriteria criteria) {
        return switch (criteria.getOperation()){
            case EQUALITY -> criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }

    public Predicate toCategoryPredicate(Join<ProductEntity, CategoryEntity> root, CriteriaBuilder criteriaBuilder, SpecSearchCriteria criteria) {
        return switch (criteria.getOperation()){
            case EQUALITY -> criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }

    public long countProductJoinCategory(String[] products, String[] categories){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<ProductEntity> productRoot = query.from(ProductEntity.class);
        Join<ProductEntity, CategoryEntity> categoryRoot = productRoot.join("category");

        List<Predicate> productPreList = new ArrayList<>();
        Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);
        for (String p : products){
            Matcher matcher = pattern.matcher(p);
            if(matcher.find()){
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                productPreList.add(toProductPredicate(productRoot, builder, criteria));
            }
        }

        List<Predicate> categoryPreList = new ArrayList<>();
        for (String c : categories){
            Matcher matcher = pattern.matcher(c);
            if(matcher.find()){
                SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                categoryPreList.add(toCategoryPredicate(categoryRoot, builder, criteria));
            }
        }

        Predicate productPre = builder.or(productPreList.toArray(new Predicate[0]));
        Predicate categoryPre = builder.or(categoryPreList.toArray(new Predicate[0]));
        Predicate finalPre = builder.and(productPre, categoryPre);

        query.select(builder.count(productRoot));
        query.where(finalPre);

        return entityManager.createQuery(query).getSingleResult();
    }
}
