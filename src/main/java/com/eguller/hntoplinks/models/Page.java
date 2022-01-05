package com.eguller.hntoplinks.models;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(builderMethodName = "pageBuilder")
public class Page {
    private String title;
}

