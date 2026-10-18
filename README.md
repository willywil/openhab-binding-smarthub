# openHAB Binding for SmartHub Energy Portals

This is an openHAB binding for pulling electricity usage data from SmartHub Coop portals (used by many municipal utilities and cooperatives, including **Athens Utilities, Alabama**).

It is based on the excellent Home Assistant custom integration: [ha-smarthub-energy-sensor](https://github.com/gagata/ha-smarthub-energy-sensor).

## Features

- Authenticate with SmartHub email/password (MFA/TOTP support planned)
- Discover service locations / accounts
- Poll hourly, daily, and monthly electricity usage (kWh)
- Channels for current / recent consumption suitable for OpenHAB Energy / Utility Meter use
- Configurable poll interval

## Limitations

- **Electricity only** (gas not supported yet)
- **Not real-time** — SmartHub data is typically delayed by hours
- Depends on the specific utility portal exposing the same API endpoints used by the HA integration
- Full production binding requires integration into the official openHAB addons build system

## Current Status

This repository contains a **functional skeleton and API client**. It is structured so it can be dropped into a fork of [openhab-addons](https://github.com/openhab/openhab-addons) and built as a proper OSGi bundle.

## How to Build & Use

1. Fork and clone `openhab/openhab-addons`
2. Copy the contents of this repo's `binding/` folder into `bundles/org.openhab.binding.smarthub/`
3. Add the module to the parent `bundles/pom.xml`
4. Build: `mvn clean install -pl :org.openhab.binding.smarthub`
5. Copy the resulting JAR from `target/` into your OpenHAB `addons/` folder or install via Marketplace once published

## Configuration (planned)

Thing configuration parameters:
- `host` (e.g. `athensalabama.smarthub.coop`)
- `email`
- `password`
- `accountId`
- `pollInterval` (minutes)

## API Notes

The client implements the same flow used by the HA integration:
- POST `/services/oauth/auth/v2` for token
- GET `/services/secured/user-data` for locations
- POST `/services/secured/utility-usage/poll` for usage data (handles PENDING status)

## License

MIT (same spirit as the HA integration it is based on).

## Disclaimer

This is an unofficial community binding. Not affiliated with NISC, SmartHub, or any utility. Use at your own risk. Test thoroughly with your portal.
