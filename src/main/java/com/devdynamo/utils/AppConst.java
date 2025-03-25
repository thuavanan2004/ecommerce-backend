package com.devdynamo.utils;

public interface AppConst {
    String SEARCH_OPERATOR = "(\\w+?)(:|<|>)(.*)";
    String SORT_BY = "(\\w+?)(:)(.*)";
    String SEARCH_SPEC_OPERATOR = "(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)";
}
