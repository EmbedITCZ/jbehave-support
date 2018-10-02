Narrative:
    As a client using message server API v2 to deliver SMS messages I want to make sure that my messages handled correctly
Description:
    This is a story containing scenarios for testing SMS API v1, v2, jms
    Some of stories uses programmable sms and has meaning only with programmable MOCK connector configured and some other env properties tuned (-Dschedule.poll-delay=200 -Dretry.initial-delay=100 -Dconnector.sms=mock -Dsms.statusCheck.interval=1:SECONDS,1:SECONDS,1:SECONDS,1:SECONDS).

Meta:
    @api v1 v2 jms
    @environments local pretest sit uat

Scenario:
    SMS-WARM-UP: Warm Up Systems
Meta:
    @id SMS-WARM-UP
    @api v2
Given an SMS for <recipient> with report level set to DELIVERY_REPORTS
When user sends an SMS
When user sends an SMS via jms using <contentType> ContentType
Then response received successfully
Examples:
|recipient|contentType|
|+420775123001|application/xml|
|+420775123002|application/xml|
|+420775123003|application/xml|
|+420775123004|application/xml|
|+420775123005|application/json|
|+420775123006|application/json|
|+420775123007|application/json|
|+420775123008|application/json|

Scenario:
    SMS-001: Send an SMS message with non-unique external id
Meta:
    @id SMS-001
    @api v2
Given an SMS for +420775123449 with external id "ext1"
Given an SMS for +420775123450 with external id "ext1"
When user sends an SMS
Then request has failed with code "Validation error"
And message with external id ext1 has message status DELIVERED

Scenario:
    SMS-002: Send an SMS message
Meta:
    @id SMS-002
    @api v1 v2 jms
    @features mssd
Given simple SMS for +420775123451
When user sends an SMS
Then response received successfully
And message status is DELIVERED
And no delivery notifications are received

Scenario:
    SMS-003: Send an SMS message with report level set to NONE
Meta:
    @id SMS-003
    @api v2 jms
    @features mssd
Given an SMS for +420775123452 with report level set to NONE
When user sends an SMS
Then response received successfully
And message status is DELIVERED
And no delivery notifications are received

Scenario:
    SMS-004: Send an SMS message with several report levels
Meta:
    @id SMS-004
    @api v2 jms
    @features mssd
Given an SMS for +420775123453 with report level set to <reportLevel>
When user sends an SMS
Then response received successfully
And following delivery notifications are received : <reports>
And message status is DELIVERED
And all delivery notifications are valid

Examples:
|reportLevel|reports|
|DELIVERY_REPORTS|DELIVERED|
|ALL|PREPARED,DELIVERED|

Scenario:
    SMS-005: Send an SMS message without priority set
Meta:
    @id SMS-005
    @api v2 jms
    @features mssd
Given an SMS for +420775123454 without priority set
When user sends an SMS
Then response received successfully
And message status is DELIVERED

Scenario:
    SMS-006: Send an SMS message with several priorities
Meta:
    @id SMS-006
    @api v1 v2 jms
    @features mssd
Given an SMS for <recipient> with <priority> priority
When user sends an SMS
Then response received successfully
And message status is DELIVERED

Examples:
|recipient|priority|
|+420775123455|LOW|
|+420775123456|MEDIUM|
|+420775123457|HIGH|
|+420775123458|IMMEDIATE|

Scenario:
    SMS-007: Send an SMS message for several report content types
Meta:
    @id SMS-007
    @api v2 jms
    @features mssd
Given an SMS for +420775123459 with report content type set to <reportContentType>
When user sends an SMS
Then response received successfully
And message status is DELIVERED

Examples:
|reportContentType|
|JSON|
|XML|

Scenario:
    SMS-008: Send an SMS message with special characters in text field
Meta:
    @id SMS-008
    @api v1 v2 jms
    @features mssd
Given an SMS for +420775123460 with text "abc 123 -/ľščťžýáíé%()"
When user sends an SMS
Then response received successfully
And message status is DELIVERED

Scenario:
    SMS-009: Send an SMS message without priority set
Meta:
    @id SMS-009
    @api v1
Given an SMS for +420775123454 without priority set
When user sends an SMS
Then request has failed with code "Validation error"

Scenario:
    SMS-010: Send an SMS message with filled sender
Meta:
    @id SMS-010
    @api v1
    @features mssd
Given an SMS for +420775123456 with sender "420775123454"
When user sends an SMS
Then response received successfully
And message status is DELIVERED


Scenario:
    SMS-011: Send an SMS message with non-existent system code
Meta:
    @id SMS-011
    @api v2
Given an SMS for +420775123454 with system code "ABC"
When user sends an SMS
Then request has failed with code "Validation error"


Scenario:
    SMS-012: Send an SMS message with existing system code
Meta:
    @id SMS-012
    @api v2 jms
    @features mssd
Given an SMS for +420775123454 with system code "BDD"
When user sends an SMS
Then response received successfully
And message status is DELIVERED


Scenario:
    SMS-013: Send an SMS message with unique external id
Meta:
    @id SMS-013
    @api v2 jms
    @features mssd
Given an SMS for +420775123455 with external id "ext8"
When user sends an SMS
Then response received successfully
And message status is DELIVERED


Scenario:
    SMS-014: Send an SMS message with existent message code
Meta:
    @id SMS-014
    @features mssd
Given an SMS for +420775123454 with message code "OTP_VERIFICATION"
When user sends an SMS
Then response received successfully
And message status is DELIVERED

Scenario:
    SMS-015: Send an SMS message with several attributes
Meta:
    @id SMS-015
    @api v2 jms
    @features mssd
Given an SMS for +420775123454 with attributes:
|attributeType|attributeValue|
|CONTRACT_NUM|317453211|
|CUID|1234|
|EMPLOYEE_CODE|surname1|
|SALESPOINT_CODE|120222|
When user sends an SMS
Then response received successfully
And message status is DELIVERED


Scenario:
    SMS-016: Send delayed SMS message
Meta:
    @id SMS-016
    @api v2 jms
    @features mssd
Given an SMS for +420775123454 with effective date set to now plus 3 seconds
When user sends an SMS
Then response received successfully
And message status is PREPARED
And wait for 6000 ms
And message status is DELIVERED

Scenario:
    SMS-017: Send an SMS message to invalid number
Meta:
    @id SMS-017
    @api v2
Given an SMS for 123 with report level set to ALL
When user sends an SMS
Then request has failed with code "Validation error"

Scenario:
    SMS-018: Send programmable SMS message simplest scenario
Meta:
    @id SMS-018
    @api v2 jms
    @features mssd
Given an SMS for +420775123449 with text "@@MOCK;SEND:IN_GATEWAY;STATUS:DELIVERED"
And with report level ALL
When user sends an SMS
Then response received successfully
And wait for 2000 ms
And following delivery notifications are received : PREPARED,DELIVERED

Scenario:
    SMS-019: Send programmable SMS message full status flow ends with delivered
Meta:
    @id SMS-019
    @api v2 jms
    @features mssd
Given an SMS for +420775123449 with text "@@MOCK;SEND:IN_GATEWAY;STATUS:IN_GATEWAY,SENT,DELIVERED"
And with report level ALL
When user sends an SMS
Then response received successfully
And wait for 3000 ms
And following delivery notifications are received : PREPARED,IN_GATEWAY,SENT,DELIVERED
And all delivery notifications are valid

Scenario:
    SMS-020: Send programmable SMS message full status flow ends with partially delivered
Meta:
    @id SMS-020
    @api v2 jms
    @features mssd
Given an SMS for +420775123449 with text "@@MOCK;SEND:IN_GATEWAY;STATUS:IN_GATEWAY,SENT,PARTIALLY_DELIVERED"
And with report level ALL
When user sends an SMS
Then response received successfully
And wait for 3000 ms
And following delivery notifications are received : PREPARED,IN_GATEWAY,SENT,PARTIALLY_DELIVERED
And all delivery notifications are valid

Scenario:
    SMS-021: Send programmable SMS message ends with error
Meta:
    @id SMS-021
    @api v2 jms
    @features mssd
Given an SMS for +420775123449 with text "@@MOCK;SEND:IN_GATEWAY;STATUS:ERROR"
And with report level ALL
When user sends an SMS
Then response received successfully
And wait for 2000 ms
And following delivery notifications are received : PREPARED,ERROR
And all delivery notifications are valid

Scenario:
    SMS-022: Send programmable SMS message ends with error after send
Meta:
    @id SMS-022
    @api v2 jms
    @features mssd
Given an SMS for +420775123449 with text "@@MOCK;SEND:IN_GATEWAY;STATUS:SENT,ERROR"
And with report level ALL
When user sends an SMS
Then response received successfully
And wait for 3000 ms
And following delivery notifications are received : PREPARED,SENT,ERROR
And all delivery notifications are valid

Scenario:
    SMS-023: Send programmable SMS message with timeout, but successfully send after
Meta:
    @id SMS-023
    @api v2 jms
    @features mssd
Given an SMS for +420775123449 with text "@@MOCK;SEND:TIMEOUT,IN_GATEWAY;STATUS:DELIVERED"
And with report level ALL
When user sends an SMS
Then response received successfully
And wait for 3000 ms
And following delivery notifications are received : PREPARED,DELIVERED
And all delivery notifications are valid

Scenario:
    SMS-024: Send an interactive SMS message
Meta:
    @id SMS-024
    @api v2 jms
    @features mssd
Given an interactive SMS for +420775123452
When user sends an SMS
Then response received successfully
And following delivery notifications are received : PREPARED,ANSWERED,DELIVERED
Then there is an SMS answer with text "SMS ANSWER" stored in DB
And all delivery notifications are valid

Scenario:
    SMS-025: Send an SMS message via JMS
Meta:
    @id SMS-025
    @api jms
    @features mssd
Given an SMS for +420775123454 with report content type set to <reportContentType>
When user sends an SMS via jms with headers:
|key|value|
|SYSTEM_CODE|BDD|
|REQUEST_ID|123|
Then response received successfully
And message status is DELIVERED
And acknowledgement notification with system code: BDD, correlation id: 123 and request status SUCCESS was received
And following delivery notifications are received : PREPARED,DELIVERED
And all delivery notifications are valid

Examples:
|reportContentType|
|JSON|
|XML|

Scenario:
    SMS-026: Send a SMS message via JMS with system code in payload and without headers
    ack notification will be send with system code from payload and without correlation id
Meta:
    @id SMS-026
    @api jms
    @features mssd
Given an SMS for +420775123454 with report content type set to <reportContentType>
When user sends an SMS via jms with headers:
|key|value|
Then response received successfully
And message status is DELIVERED
And acknowledgement notification with system code: BDD, correlation id:  and request status SUCCESS was received
And following delivery notifications are received : PREPARED,DELIVERED
And all delivery notifications are valid

Examples:
|reportContentType|
|JSON|
|XML|

Scenario:
    SMS-027: Send an SMS message via JMS without system code in payload and with defined headers
    system code is mandatory field in payload so unmarshalling should fail -> no delivery notification and acknowledgement notification with status error
Meta:
    @id SMS-027
    @api jms
    @features mssd
Given an SMS for +420775123454 with system code  and report content type set to <reportContentType>
When user sends an SMS via jms with headers:
|key|value|
|SYSTEM_CODE|BDD|
|REQUEST_ID|123|
Then response received successfully
And acknowledgement notification with system code: BDD, correlation id: 123 and request status ERROR was received
And no delivery notifications are received
And all acknowledgement notifications are valid

Examples:
|reportContentType|
|JSON|
|XML|

Scenario:
    SMS-028: Send an SMS message via JMS without system code,
    it's mandatory field so unmarshalling should fail -> no delivery notification and acknowledgement notification with status error
Meta:
    @id SMS-028
    @api jms
    @features mssd
Given an SMS for +420775123454 with system code  and report content type set to <reportContentType>
When user sends an SMS via jms with headers:
|key|value|
|REQUEST_ID|123|
Then response received successfully
And acknowledgement notification with system code: , correlation id: 123 and request status ERROR was received
And no delivery notifications are received
And all acknowledgement notifications are valid

Examples:
|reportContentType|
|JSON|
|XML|

Scenario:
    SMS-029: Send an SMS message via JMS without system code and correlation id,
    it's manatory field so unmarshalling should fail -> no delivery notification
    and because there is no system or correlation id specified there will be no ack notification too
Meta:
    @id SMS-029
    @api jms
    @features mssd
Given an SMS for +420775123454 with system code  and report content type set to <reportContentType>
When user sends an SMS via jms with headers:
|key|value|
Then response received successfully
And no acknowledgement notifications are received
And no delivery notifications are received

Examples:
|reportContentType|
|JSON|
|XML|

Scenario:
    SMS-030: Send an SMS message using content type
Meta:
    @id SMS-030
    @api jms
    @features mssd
Given simple SMS for +420775123451
When user sends an SMS via jms using <contentType> ContentType
Then response received successfully
And message status is DELIVERED
And no delivery notifications are received

Examples:
|contentType|
|application/json|
|application/xml|

Scenario:
    SMS-031: Store used SMS gateway in message log attributes
Meta:
    @id SMS-031
    @api jms
    @features mssd
    @ignore
Given simple SMS for +420775123451
When user sends an SMS via jms using <contentType> ContentType
Then response received successfully
And message status is DELIVERED
And message log attributes:
|attributeType|attributeValue|
|USED_SMS_PROVIDER|mock|

Examples:
|contentType|
|application/json|
|application/xml|
