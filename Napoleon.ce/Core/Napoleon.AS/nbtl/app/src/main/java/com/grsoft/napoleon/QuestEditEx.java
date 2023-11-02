package com.grsoft.napoleon;

import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.QuestionItemEx;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class QuestEditEx extends QuestEdit{
    @Override
    public boolean skipQuestItem(QuestionItem i) {
        if (((QuestionItemEx)i).clients.trim().length() == 0)
            return  false;

        String[] clients = ((QuestionItemEx)i).clients.split(",");
        Set<String> set = new HashSet<>(Arrays.asList(clients));

        return !set.contains(answer.getData().id);
    }
}
