package org.openhab.binding.smarthub.internal.handler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openhab.binding.smarthub.internal.SmartHubBindingConstants;
import org.openhab.binding.smarthub.internal.api.SmartHubApiClient;

/**
 * The {@link SmartHubAccountHandler} is responsible for handling commands and
 * updating the state of the SmartHub account Thing.
 */
@NonNullByDefault
public class SmartHubAccountHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(SmartHubAccountHandler.class);
    private SmartHubApiClient apiClient;

    public SmartHubAccountHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // Read-only binding — no commands supported yet
    }

    @Override
    public void initialize() {
        String host = (String) getConfig().get(SmartHubBindingConstants.CONFIG_HOST);
        String email = (String) getConfig().get(SmartHubBindingConstants.CONFIG_EMAIL);
        String password = (String) getConfig().get(SmartHubBindingConstants.CONFIG_PASSWORD);

        if (host == null || email == null || password == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Missing required configuration");
            return;
        }

        apiClient = new SmartHubApiClient(host, email, password);

        if (apiClient.authenticate()) {
            updateStatus(ThingStatus.ONLINE);
            // TODO: schedule polling and update channels with energy data
            logger.info("SmartHub account online");
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Authentication failed");
        }
    }
}
