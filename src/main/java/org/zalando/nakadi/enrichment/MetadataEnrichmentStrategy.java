package org.zalando.nakadi.enrichment;

import org.zalando.nakadi.domain.BatchItem;
import org.zalando.nakadi.domain.EventType;
import org.zalando.nakadi.exceptions.EnrichmentException;
import org.zalando.nakadi.util.FlowIdUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONException;
import org.json.JSONObject;

public class MetadataEnrichmentStrategy implements EnrichmentStrategy {
    @Override
    public void enrich(final BatchItem batchItem, final EventType eventType) throws EnrichmentException {
        try {
            final JSONObject metadata = batchItem.getEvent().getJSONObject("metadata");

            setReceivedAt(metadata);
            setEventTypeName(metadata, eventType);
            setFlowId(metadata);
            setPartition(metadata, batchItem);
        } catch (final JSONException e) {
            throw new EnrichmentException("enrichment error", e);
        }
    }

    private void setFlowId(final JSONObject metadata) {
        metadata.put("flow_id", FlowIdUtils.peek());
    }

    private void setEventTypeName(final JSONObject metadata, final EventType eventType) {
        metadata.put("event_type", eventType.getName());
    }

    private void setReceivedAt(final JSONObject metadata) {
        final DateTime dateTime = new DateTime(DateTimeZone.UTC);
        metadata.put("received_at", dateTime.toString());
    }

    public void setPartition(final JSONObject metadata, final BatchItem batchItem) {
        metadata.put("partition", batchItem.getPartition());
    }
}
