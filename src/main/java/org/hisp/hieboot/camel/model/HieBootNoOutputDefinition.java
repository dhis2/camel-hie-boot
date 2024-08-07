package org.hisp.hieboot.camel.model;

import org.apache.camel.model.NoOutputDefinition;
import org.apache.camel.model.ProcessorDefinition;

public class HieBootNoOutputDefinition extends NoOutputDefinition<HieBootNoOutputDefinition> {

    public HieBootNoOutputDefinition() {
        disabled(true);
    }

    @Override
    public String getShortName() {
        return "noOutput";
    }

    @Override
    public ProcessorDefinition<HieBootNoOutputDefinition> copyDefinition() {
        return new HieBootNoOutputDefinition();
    }
}
