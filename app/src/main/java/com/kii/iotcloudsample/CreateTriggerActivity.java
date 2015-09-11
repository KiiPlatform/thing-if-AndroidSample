package com.kii.iotcloudsample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.kii.iotcloud.IoTCloudAPI;
import com.kii.iotcloudsample.fragments.wizard.CreateTriggerCommandFragment;
import com.kii.iotcloudsample.fragments.wizard.CreateTriggerPredicateFragment;
import com.kii.iotcloudsample.fragments.wizard.CreateTriggersWhenFragment;
import com.kii.iotcloudsample.fragments.wizard.WizardFragment;
import com.kii.iotcloudsample.model.Trigger;
import com.kii.iotcloudsample.promise_api_wrapper.IoTCloudPromiseAPIWrapper;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

public class CreateTriggerActivity extends AppCompatActivity implements WizardFragment.WizardController {

    public static final String TAG = CreateTriggerActivity.class.getSimpleName();
    private static final int WIZARD_PAGE_SIZE = 3;
    private IoTCloudAPI api;
    private WizardPagerAdapter adapter;
    private ViewPager viewPager;
    private Button nextButton;
    private Button previousButton;
    private Trigger editingTrigger;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trigger);
        Intent i = getIntent();
        this.api = (IoTCloudAPI)i.getParcelableExtra("IoTCloudAPI");
        this.adapter = new WizardPagerAdapter(getSupportFragmentManager());
        this.viewPager = (ViewPager)findViewById(R.id.step_pager);
        this.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                View currentFocus = CreateTriggerActivity.this.getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager imm = (InputMethodManager) CreateTriggerActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
                if (currentPosition < position) {
                    ((WizardFragment)adapter.instantiateItem(viewPager, currentPosition)).onInactivate(WizardFragment.EXIT_NEXT);
                } else {
                    ((WizardFragment)adapter.instantiateItem(viewPager, currentPosition)).onInactivate(WizardFragment.EXIT_PREVIOUS);
                }
                WizardFragment wizardFragment = (WizardFragment) adapter.instantiateItem(viewPager, position);
                wizardFragment.onActivate();
                currentPosition = position;
            }
        });
        this.viewPager.setAdapter(this.adapter);
        this.nextButton = (Button)findViewById(R.id.wizard_next_button);
        this.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getCurrentItem();
                WizardFragment wizardFragment = (WizardFragment) adapter.instantiateItem(viewPager, position);
                if (position + 1 < WIZARD_PAGE_SIZE) {
                    position++;
                    viewPager.setCurrentItem(position);
                    wizardFragment = (WizardFragment) adapter.getItem(position);
                    nextButton.setText(wizardFragment.getNextButtonText());
                    previousButton.setText(wizardFragment.getPreviousButtonText());

                } else if (position + 1 == WIZARD_PAGE_SIZE) {
                    wizardFragment.onInactivate(WizardFragment.EXIT_NEXT);
                    IoTCloudPromiseAPIWrapper wp = new IoTCloudPromiseAPIWrapper(api);
                    wp.postNewTrigger(AppConstants.SCHEMA_NAME, AppConstants.SCHEMA_VERSION, editingTrigger.getActions(), editingTrigger.getPredicate()).then(new DoneCallback<com.kii.iotcloud.trigger.Trigger>() {
                        @Override
                        public void onDone(com.kii.iotcloud.trigger.Trigger result) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    }, new FailCallback<Throwable>() {
                        @Override
                        public void onFail(Throwable result) {
                            Toast.makeText(CreateTriggerActivity.this, "Failed to create new editingTrigger: !" + result.getMessage(), Toast.LENGTH_LONG).show();
                            setResult(Activity.RESULT_CANCELED);
                            finish();
                        }
                    });
                }
            }
        });
        this.nextButton.setText("Next");
        this.previousButton = (Button)findViewById(R.id.wizard_previous_button);
        this.previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getCurrentItem();
                WizardFragment wizardFragment = (WizardFragment) adapter.instantiateItem(viewPager, position);
                if (position > 0) {
                    position--;
                    viewPager.setCurrentItem(position);
                    wizardFragment = (WizardFragment) adapter.getItem(position);
                    nextButton.setText(wizardFragment.getNextButtonText());
                    previousButton.setText(wizardFragment.getPreviousButtonText());
                } else {
                    finish();
                }
            }
        });
        this.previousButton.setText("Cancel");

        // TODO: Supports to edit existing editingTrigger
        this.editingTrigger = new Trigger();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("IoTCloudAPI", this.api);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.api = savedInstanceState.getParcelable("IoTCloudAPI");
    }
    public IoTCloudAPI getApi() {
        return this.api;
    }

    @Override
    public void setNextButtonEnabled(boolean enabled) {
        this.nextButton.setEnabled(enabled);
    }

    private class WizardPagerAdapter extends FragmentStatePagerAdapter {
        private static final int PAGE_COMMAND_SETTING  = 0;
        private static final int PAGE_PREDICATE_SETTING = 1;
        private static final int PAGE_TRIGGER_WHEN_SETTING = 2;

        public WizardPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            WizardFragment fragment = null;
            switch (position) {
                case PAGE_COMMAND_SETTING:
                    fragment = CreateTriggerCommandFragment.newFragment(api);
                    break;
                case PAGE_PREDICATE_SETTING:
                    fragment = CreateTriggerPredicateFragment.newFragment(api);
                    break;
                case PAGE_TRIGGER_WHEN_SETTING:
                    fragment = CreateTriggersWhenFragment.newFragment(api);
                    break;
            }
            fragment.setController(CreateTriggerActivity.this);
            fragment.setEditingTrigger(editingTrigger);
            return fragment;
        }
        @Override
        public int getCount() {
            return WIZARD_PAGE_SIZE;
        }
    }
}
