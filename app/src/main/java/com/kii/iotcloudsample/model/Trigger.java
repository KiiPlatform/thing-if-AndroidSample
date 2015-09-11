package com.kii.iotcloudsample.model;

import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.trigger.StatePredicate;
import com.kii.iotcloud.trigger.TriggersWhen;

import java.util.ArrayList;
import java.util.List;

public class Trigger {
    private List<Action> actions = new ArrayList<Action>();
    private StatePredicate predicate = null;

    public Trigger() {
    }
    public Trigger(com.kii.iotcloud.trigger.Trigger trigger) {
        for (Action action : trigger.getCommand().getActions()) {
            this.actions.add(action);
        }
        // TODO:Supports Schedule Predicate
        this.predicate = (StatePredicate)trigger.getPredicate();
    }

    public void clearActions() {
        this.actions.clear();
    }
    public void addAction(Action action) {
        this.actions.add(action);
    }
    public List<Action> getActions() {
        return this.actions;
    }
    public StatePredicate getPredicate() {
        return this.predicate;
    }
    public void setPredicate(StatePredicate predicate) {
        this.predicate = predicate;
    }
}
