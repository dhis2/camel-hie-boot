package org.hisp.hieboot.camel;

import org.apache.camel.Exchange;

public interface HieExchange extends Exchange {

    String REPLAY_CHECKPOINT_ROUTE_ID = "CamelHieReplayCheckpointRouteId";
    String REPLAY_CHECKPOINT_MESSAGE_ID = "CamelHieReplayCheckpointMessageId";
}
