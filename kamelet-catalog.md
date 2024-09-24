# Camel HIE Boot Kamelet Catalog

- [OpenHIM Mediator Heartbeat Source](#openhim-mediator-heartbeat-source)
- [OpenHIM Mediator Register Source](#openhim-mediator-register-source)
- [RapidPro Create Field Sink](#rapidpro-create-field-sink)
- [RapidPro Create Group Sink](#rapidpro-create-group-sink)
- [RapidPro Create Or Update Contact Sink](#rapidpro-create-or-update-contact-sink)
- [RapidPro Get Contacts Sink](#rapidpro-get-contacts-sink)
- [RapidPro Get Fields Sink](#rapidpro-get-fields-sink)
- [RapidPro Get Flow Runs Sink](#rapidpro-get-flow-runs-sink)
- [RapidPro Get Groups Sink](#rapidpro-get-groups-sink)
- [RapidPro Send Broadcast Sink](#rapidpro-send-broadcast-sink)
- [Replay Checkpoint Action](#replay-checkpoint-action)

## OpenHIM Mediator Heartbeat Source

**Provided by: HISP Centre**

**Support Level for this Kamelet is: Stable**

Send a heartbeat to OpenHIM.

### Configuration Properties

The following table summarises the configuration properties available for the `hie-openhim-mediator-heartbeat-source` Kamelet:

| Property             | Name                   | Description                                                                                                                                                                                                                                                  | Required | Type   | Default                         | Example                         |
| -------------------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------- | ------ | ------------------------------- | ------------------------------- |
| openHimUrl           | OpenHIM URL            | HTTP/S URL of the OpenHIM Core server.                                                                                                                                                                                                                       | true     | string |                                 | https://localhost:8080          |
| openHimUsername      | OpenHIM Username       | Username of the OpenHIM user account performing this operation.                                                                                                                                                                                              | true     | string |                                 | root@openhim.org                |
| openHimPassword      | OpenHIM Password       | Password of the OpenHIM user account performing this operation.                                                                                                                                                                                              | true     | string |                                 | openhim-password                |
| httpClientConfigurer | HTTP Client Configurer | Reference to custom `org.apache.camel.component.http.HttpClientConfigurer` that configures the `HttpClient` to be used. This property should be set to `#selfSignedHttpClientConfigurer` for an OpenHIM server presenting a self-signed TLS/SSL certificate. | false    | string |                                 | #selfSignedHttpClientConfigurer |
| mediatorUrn          | Mediator URN           | Unique identifier to identify the mediator.                                                                                                                                                                                                                  | false    | string | urn:mediator:camel-hie-mediator |                                 |

## OpenHIM Mediator Register Source

**Provided by: HISP Centre**

**Support Level for this Kamelet is: Stable**

Register a mediator with OpenHIM.

### Configuration Properties

The following table summarises the configuration properties available for the `hie-openhim-mediator-register-source` Kamelet:

| Property                   | Name                          | Description                                                                                                                                                                                                                                                  | Required | Type    | Default                         | Example                         |
| -------------------------- | ----------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------- | ------- | ------------------------------- | ------------------------------- |
| openHimUrl                 | OpenHIM URL                   | HTTP/S URL of the OpenHIM Core server.                                                                                                                                                                                                                       | true     | string  |                                 |                                 |
| openHimUsername            | OpenHIM Username              | Username of the OpenHIM user account performing this operation.                                                                                                                                                                                              | true     | string  |                                 |                                 |
| openHimPassword            | OpenHIM Password              | Password of the OpenHIM user account performing this operation.                                                                                                                                                                                              | true     | string  |                                 |                                 |
| httpClientConfigurer       | HTTP Client Configurer        | Reference to custom `org.apache.camel.component.http.HttpClientConfigurer` that configures the `HttpClient` to be used. This property should be set to `#selfSignedHttpClientConfigurer` for an OpenHIM server presenting a self-signed TLS/SSL certificate. | false    | string  |                                 | #selfSignedHttpClientConfigurer |
| mediatorUrn                | Mediator URN                  | Unique identifier to identify this mediator.                                                                                                                                                                                                                 | false    | string  | urn:mediator:camel-hie-mediator |                                 |
| mediatorVersion            | Mediator Version              | Indicates the version of this mediator.                                                                                                                                                                                                                      | false    | string  | 1.0.0                           |                                 |
| mediatorName               | Mediator Name                 | Name of this mediator.                                                                                                                                                                                                                                       | false    | string  | Camel HIE Mediator              |                                 |
| defaultChannelName         | Default Channel Name          | Name of the default channel to install.                                                                                                                                                                                                                      | false    | string  | camel-hie-channel               |                                 |
| defaultChannelMethods      | Default Channel Methods       | HTTP methods supported by the default channel.                                                                                                                                                                                                               | false    | array   | DELETE, GET, PATCH, POST, PUT   |                                 |
| defaultChannelUrlPattern   | Default Channel URL Pattern   | Describes which incoming requests should be sent down the default channel.                                                                                                                                                                                   | false    | string  | ^/                              |                                 |
| defaultChannelAuthType     | Default Channel Auth Type     | Whether the default channel is private or public.                                                                                                                                                                                                            | false    | string  | public                          |                                 |
| defaultChannelRouteName    | Default Channel Route Name    | Name of the default route.                                                                                                                                                                                                                                   | false    | string  | Camel HIE Route                 |                                 |
| defaultChannelRouteHost    | Default Channel Route Host    | Host where the default route should go to.                                                                                                                                                                                                                   | false    | string  | localhost                       |                                 |
| defaultChannelRoutePort    | Default Channel Route Port    | Port where the default route should go to.                                                                                                                                                                                                                   | false    | string  | 9070                            |                                 |
| defaultChannelRoutePath    | Default Channel Route Path    | Path the default route should follow.                                                                                                                                                                                                                        | false    | string  | /                               |                                 |
| defaultChannelRoutePrimary | Default Channel Route Primary | Whether the default route is primary: setting a route to primary indicates that this is the first route to check and is the primary endpoint to reach.                                                                                                       | false    | boolean | true                            |                                 |
| defaultChannelRouteSecured | Default Channel Route Secured |                                                                                                                                                                                                                                                              | false    | boolean | false                           |                                 |
| endpointName               | Endpoint Name                 |                                                                                                                                                                                                                                                              | false    | string  | Camel HIE Endpoint              |                                 |
| endpointType               | Endpoint Type                 |                                                                                                                                                                                                                                                              | false    | string  | http                            |                                 |
| endpointSecured            | Endpoint Secured              |                                                                                                                                                                                                                                                              | false    | boolean | false                           |                                 |
| endpointHost               | Endpoint Host                 |                                                                                                                                                                                                                                                              | false    | string  | localhost                       |                                 |
| endpointPort               | Endpoint Port                 |                                                                                                                                                                                                                                                              | false    | string  | 9070                            |                                 |
| endpointStatus             | Endpoint Status               |                                                                                                                                                                                                                                                              | false    | string  | enabled                         |                                 |
| endpointPath               | Endpoint Path                 |                                                                                                                                                                                                                                                              | false    | string  | /                               |                                 |

## RapidPro Create Field Sink

**Provided by: HISP Centre**

**Support Level for this Kamelet is: Stable**

Create a field on RapidPro.

### Configuration Properties

The following table summarises the configuration properties available for the `hie-rapidpro-create-field-sink` Kamelet:

| Property         | Name               | Description | Required | Type   | Default | Example |
| ---------------- | ------------------ | ----------- | -------- | ------ | ------- | ------- |
| rapidProApiUrl   | RapidPro API URL   |             | true     | string |         |         |
| rapidProApiToken | RapidPro API Token |             | true     | string |         |         |

### Configuration Input Headers

The following table summarises the input headers available for the `hie-rapidpro-create-field-sink` Kamelet:

| Header | Name  | Description | Required | Type | Default | Example |
| ------ | ----- | ----------- | -------- | ---- | ------- | ------- |
| label  | Label |             | false    |      |         |         |
| type   | Type  |             | false    |      |         |         |

## RapidPro Create Group Sink

**Provided by: HISP Centre**

**Support Level for this Kamelet is: Stable**

Create a contact group on RapidPro.

### Configuration Properties

The following table summarises the configuration properties available for the `hie-rapidpro-create-group-sink` Kamelet:

| Property         | Name               | Description | Required | Type   | Default | Example |
| ---------------- | ------------------ | ----------- | -------- | ------ | ------- | ------- |
| rapidProApiUrl   | RapidPro API URL   |             | true     | string |         |         |
| rapidProApiToken | RapidPro API Token |             | true     | string |         |         |

### Configuration Input Headers

The following table summarises the input headers available for the `hie-rapidpro-create-group-sink` Kamelet:

| Header | Name | Description | Required | Type | Default | Example |
| ------ | ---- | ----------- | -------- | ---- | ------- | ------- |
| name   | Name |             | false    |      |         |         |

## RapidPro Create Or Update Contact Sink

**Provided by: HISP Centre**

**Support Level for this Kamelet is: Stable**

Add a contact to RapidPro or update it.

### Configuration Properties

The following table summarises the configuration properties available for the `hie-rapidpro-create-or-update-contact-sink` Kamelet:

| Property          | Name                 | Description | Required | Type   | Default | Example |
| ----------------- | -------------------- | ----------- | -------- | ------ | ------- | ------- |
| rapidProApiUrl    | RapidPro API URL     |             | true     | string |         |         |
| rapidProApiToken  | RapidPro API Token   |             | true     | string |         |         |
| httpOkStatusRange | HTTP OK Status Range |             | false    | string | 200-299 |         |

### Configuration Input Headers

The following table summarises the input headers available for the `hie-rapidpro-create-or-update-contact-sink` Kamelet:

| Header            | Name               | Description | Required | Type | Default | Example |
| ----------------- | ------------------ | ----------- | -------- | ---- | ------- | ------- |
| uuid              | Contact UUID       |             | false    |      |         |         |
| urn               | Contact URN        |             | false    |      |         |         |
| contactName       | Contact Name       |             | false    |      |         |         |
| phoneNumber       | Phone Number       |             | false    |      |         |         |
| telegram          | Telegram           |             | false    |      |         |         |
| whatsApp          | WhatsApp           |             | false    |      |         |         |
| facebookMessenger | Facebook Messenger |             | false    |      |         |         |
| twitterId         | Twitter ID         |             | false    |      |         |         |
| groups            | Groups             |             | false    |      |         |         |
| fields            | Fields             |             | false    |      |         |         |

## RapidPro Get Contacts Sink

**Provided by: HISP Centre**

**Support Level for this Kamelet is: Stable**

Get contacts from RapidPro.

### Configuration Properties

The following table summarises the configuration properties available for the `hie-rapidpro-get-contacts-sink` Kamelet:

| Property         | Name               | Description | Required | Type   | Default | Example |
| ---------------- | ------------------ | ----------- | -------- | ------ | ------- | ------- |
| rapidProApiUrl   | RapidPro API URL   |             | true     | string |         |         |
| rapidProApiToken | RapidPro API Token |             | true     | string |         |         |

### Configuration Input Headers

The following table summarises the input headers available for the `hie-rapidpro-get-contacts-sink` Kamelet:

| Header           | Name          | Description | Required | Type | Default | Example |
| ---------------- | ------------- | ----------- | -------- | ---- | ------- | ------- |
| uuid             | UUID          |             | false    |      |         |         |
| urn              | URNs          |             | false    |      |         |         |
| group            | Groups        |             | false    |      |         |         |
| beforeModifiedOn | Before        |             | false    |      |         |         |
| afterModifiedOn  | After         |             | false    |      |         |         |
| nextPageUrl      | Next Page URL |             | false    |      |         |         |

## RapidPro Get Fields Sink

**Provided by: HISP Centre**

**Support Level for this Kamelet is: Stable**

Get fields from RapidPro.

### Configuration Properties

The following table summarises the configuration properties available for the `hie-rapidpro-get-fields-sink` Kamelet:

| Property         | Name               | Description | Required | Type   | Default | Example |
| ---------------- | ------------------ | ----------- | -------- | ------ | ------- | ------- |
| rapidProApiUrl   | RapidPro API URL   |             | true     | string |         |         |
| rapidProApiToken | RapidPro API Token |             | true     | string |         |         |

### Configuration Input Headers

The following table summarises the input headers available for the `hie-rapidpro-get-fields-sink` Kamelet:

| Header      | Name          | Description | Required | Type | Default | Example |
| ----------- | ------------- | ----------- | -------- | ---- | ------- | ------- |
| key         | Key           |             | false    |      |         |         |
| nextPageUrl | Next Page URL |             | false    |      |         |         |

## RapidPro Get Flow Runs Sink

**Provided by: HISP Centre**

**Support Level for this Kamelet is: Stable**

Returns the flow runs for your organization, filtering them as needed. Note that you cannot filter by flow and contact at the same time.

### Configuration Properties

The following table summarises the configuration properties available for the `hie-rapidpro-get-flow-runs-sink` Kamelet:

| Property         | Name               | Description | Required | Type   | Default | Example |
| ---------------- | ------------------ | ----------- | -------- | ------ | ------- | ------- |
| rapidProApiUrl   | RapidPro API URL   |             | true     | string |         |         |
| rapidProApiToken | RapidPro API Token |             | true     | string |         |         |

### Configuration Input Headers

The following table summarises the input headers available for the `hie-rapidpro-get-flow-runs-sink` Kamelet:

| Header      | Name          | Description | Required | Type | Default | Example |
| ----------- | ------------- | ----------- | -------- | ---- | ------- | ------- |
| uuid        | UUID          |             | false    |      |         |         |
| flow        | URNs          |             | false    |      |         |         |
| contact     | Groups        |             | false    |      |         |         |
| responded   | Groups        |             | false    |      |         |         |
| before      | Before        |             | false    |      |         |         |
| after       | After         |             | false    |      |         |         |
| reverse     | Groups        |             | false    |      |         |         |
| nextPageUrl | Next Page URL |             | false    |      |         |         |

## RapidPro Get Groups Sink

**Provided by: HISP Centre**

**Support Level for this Kamelet is: Stable**

Returns the list of contact groups for your organisation, in the order of last created.

### Configuration Properties

The following table summarises the configuration properties available for the `hie-rapidpro-get-groups-sink` Kamelet:

| Property         | Name               | Description | Required | Type   | Default | Example |
| ---------------- | ------------------ | ----------- | -------- | ------ | ------- | ------- |
| rapidProApiUrl   | RapidPro API URL   |             | true     | string |         |         |
| rapidProApiToken | RapidPro API Token |             | true     | string |         |         |

### Configuration Input Headers

The following table summarises the input headers available for the `hie-rapidpro-get-groups-sink` Kamelet:

| Header      | Name          | Description | Required | Type | Default | Example |
| ----------- | ------------- | ----------- | -------- | ---- | ------- | ------- |
| uuid        | UUID          |             | false    |      |         |         |
| name        | Name          |             | false    |      |         |         |
| nextPageUrl | Next Page URL |             | false    |      |         |         |

## RapidPro Send Broadcast Sink

**Provided by: HISP Centre**

**Support Level for this Kamelet is: Stable**

Send broadcast from RapidPro.

### Configuration Properties

The following table summarises the configuration properties available for the `hie-rapidpro-send-broadcast-sink` Kamelet:

| Property         | Name               | Description | Required | Type   | Default | Example |
| ---------------- | ------------------ | ----------- | -------- | ------ | ------- | ------- |
| rapidProApiUrl   | RapidPro API URL   |             | true     | string |         |         |
| rapidProApiToken | RapidPro API Token |             | true     | string |         |         |

### Configuration Input Headers

The following table summarises the input headers available for the `hie-rapidpro-send-broadcast-sink` Kamelet:

| Header        | Name                         | Description | Required | Type | Default | Example |
| ------------- | ---------------------------- | ----------- | -------- | ---- | ------- | ------- |
| urns          | URNs                         |             | false    |      |         |         |
| contacts      | Contacts                     |             | false    |      |         |         |
| groups        | Contacts                     |             | false    |      |         |         |
| text          | Text                         |             | true     |      |         |         |
| base_language | Default Translation Language |             | false    |      |         |         |

## Replay Checkpoint Action

**Provided by: HISP Centre**

**Support Level for this Kamelet is: Stable**

Records and replays messages.

### Configuration Properties

The following table summarises the configuration properties available for the `hie-replay-checkpoint-action` Kamelet:

| Property          | Name                | Description | Required | Type   | Default            | Example |
| ----------------- | ------------------- | ----------- | -------- | ------ | ------------------ | ------- |
| replayChannelName | Replay Channel Name |             | false    | string | replay-{{routeId}} |         |
