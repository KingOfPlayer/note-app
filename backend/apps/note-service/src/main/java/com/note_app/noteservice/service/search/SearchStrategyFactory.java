package com.note_app.noteservice.service.search;

import com.note_app.commonutils.exception.BadRequestException;
import com.note_app.commonutils.exception.ErrorMessages;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SearchStrategyFactory {

    private final Map<String, SearchStrategy> strategies = new HashMap<>();

    public SearchStrategyFactory(List<SearchStrategy> strategyList) {
        for (SearchStrategy s : strategyList) {
            strategies.put(s.getType().toLowerCase(), s);
        }
    }

    public SearchStrategy resolve(String type) {
        if (type == null || type.isBlank()) {
            return strategies.get("all");
        }
        SearchStrategy strategy = strategies.get(type.toLowerCase());
        if (strategy == null) {
            throw new BadRequestException(ErrorMessages.withId(ErrorMessages.SEARCH_TYPE_UNKNOWN, type));
        }
        return strategy;
    }
}
