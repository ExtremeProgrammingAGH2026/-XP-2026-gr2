package org.example.cucumber.steps;

import io.cucumber.java.After;
import org.example.TestContext;

public class Hooks {

    private final TestContext ctx;

    public Hooks(TestContext ctx) {
        this.ctx = ctx;
    }

    @After
    public void cleanUp() {
        ctx.cleanup();
    }
}