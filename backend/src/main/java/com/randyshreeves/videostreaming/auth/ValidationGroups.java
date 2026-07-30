package com.randyshreeves.videostreaming.auth;

import jakarta.validation.GroupSequence;

@GroupSequence({
        ValidationGroups.Required.class,
        ValidationGroups.Length.class,
        ValidationGroups.Format.class
})
public interface ValidationGroups {

    interface Required {}

    interface Length {}

    interface Format {}
}
