package com.yushan.backend.service.report;

public abstract class ValidationHandler {

    private ValidationHandler next;

    public ValidationHandler setNext(ValidationHandler next) {
        this.next = next;
        return next;
    }

    public void handle(ReportContext context) {
        doHandle(context);
        if (next != null) {
            next.handle(context);
        }
    }

    protected abstract void doHandle(ReportContext context);
}


