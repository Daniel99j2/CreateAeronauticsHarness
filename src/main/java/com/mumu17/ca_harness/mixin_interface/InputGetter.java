package com.mumu17.ca_harness.mixin_interface;

public interface InputGetter {
    Inputs ca_harness$getInputs();

    public static record Inputs(boolean w, boolean a, boolean s, boolean d) {}
}
