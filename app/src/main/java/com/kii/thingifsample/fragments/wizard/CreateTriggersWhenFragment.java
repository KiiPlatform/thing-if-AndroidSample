package com.kii.thingifsample.fragments.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.trigger.TriggersWhen;
import com.kii.thingifsample.R;

import java.util.Arrays;

public class CreateTriggersWhenFragment extends WizardFragment {

    private static final String DESCRIPTION_CONDITION_TRUE = "TriggerManager checks the current state and Command is executed when current state is evaluated as TRUE.";
    private static final String DESCRIPTION_CONDITION_FALSE_TO_TRUE = "TriggerManager checks previous state and current state. Command is executed when previous state is evaluated as false and current one is evaluated as true.";
    private static final String DESCRIPTION_CONDITION_CHANGED = "TriggerManager checks previous state and current state. Command is executed when previous state and current state is evaluated in different value. (i.e. (true, false) or (false, true))";


    private ThingIFAPI api;
    private Spinner spinTriggersWhen;
    private TextView txtDescription;

    public static CreateTriggersWhenFragment newFragment(ThingIFAPI api) {
        CreateTriggersWhenFragment fragment = new CreateTriggersWhenFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable("ThingIFAPI", api);
        fragment.setArguments(arguments);
        return fragment;
    }

    public CreateTriggersWhenFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ThingIFAPI", this.api);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.api = savedInstanceState.getParcelable("ThingIFAPI");
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.api = arguments.getParcelable("ThingIFAPI");
        }
        View view = inflater.inflate(R.layout.create_trigger_when_view, null);
        this.spinTriggersWhen = (Spinner)view.findViewById(R.id.spinTriggersWhen);
        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item);
        for (TriggersWhen when : TriggersWhen.values()) {
            adapter.add(when);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinTriggersWhen.setAdapter(adapter);
        this.spinTriggersWhen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                TriggersWhen when = (TriggersWhen) spinner.getSelectedItem();
                switch (when) {
                    case CONDITION_TRUE:
                        txtDescription.setText(DESCRIPTION_CONDITION_TRUE);
                        break;
                    case CONDITION_FALSE_TO_TRUE:
                        txtDescription.setText(DESCRIPTION_CONDITION_FALSE_TO_TRUE);
                        break;
                    case CONDITION_CHANGED:
                        txtDescription.setText(DESCRIPTION_CONDITION_CHANGED);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        this.txtDescription = (TextView)view.findViewById(R.id.textDescription);
        return view;
    }

    @Override
    public void onActivate() {
        TriggersWhen when = this.editingTrigger.getTriggersWhen();
        if (when == null) {
            when = TriggersWhen.CONDITION_TRUE;
        }
        int index = Arrays.binarySearch(TriggersWhen.values(), when);
        this.spinTriggersWhen.setSelection(index);
    }
    @Override
    public void onInactivate(int exitCode) {
        this.editingTrigger.setTriggersWhen((TriggersWhen)this.spinTriggersWhen.getSelectedItem());
    }
    @Override
    public String getNextButtonText() {
        return "Next";
    }
    @Override
    public String getPreviousButtonText() {
        return "Previous";
    }

}
