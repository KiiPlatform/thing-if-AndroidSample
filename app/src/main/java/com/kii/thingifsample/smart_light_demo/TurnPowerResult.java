package com.kii.thingifsample.smart_light_demo;

import com.kii.thingif.command.ActionResult;

public class TurnPowerResult extends ActionResult {
    @Override
    public String getActionName() {
        return "turnPower";
    }
}
