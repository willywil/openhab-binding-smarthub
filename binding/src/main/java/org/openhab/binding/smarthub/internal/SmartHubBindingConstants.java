package org.openhab.binding.smarthub.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link SmartHubBindingConstants} class defines common constants.
 */
@NonNullByDefault
public class SmartHubBindingConstants {

    public static final String BINDING_ID = "smarthub";

    // Thing Type
    public static final ThingTypeUID THING_TYPE_ACCOUNT = new ThingTypeUID(BINDING_ID, "account");

    // Channels
    public static final String CHANNEL_ENERGY_HOURLY = "energyHourly";
    public static final String CHANNEL_ENERGY_DAILY = "energyDaily";
    public static final String CHANNEL_ENERGY_MONTHLY = "energyMonthly";
    public static final String CHANNEL_LAST_UPDATE = "lastUpdate";

    // Config parameters
    public static final String CONFIG_HOST = "host";
    public static final String CONFIG_EMAIL = "email";
    public static final String CONFIG_PASSWORD = "password";
    public static final String CONFIG_ACCOUNT_ID = "accountId";
    public static final String CONFIG_POLL_INTERVAL = "pollInterval";
}
