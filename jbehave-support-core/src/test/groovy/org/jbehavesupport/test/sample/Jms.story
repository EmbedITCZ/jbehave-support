This story aims to test JMS steps

Scenario: Publish a JMS message with XML body
Given [NameRequest] data for JMS broker [TEST]:
| name                     | data                  |
| name                     | test                  |
| CUID                     | {RANDOM_STRING:7}     |
| address.0                | {RANDOM_EMAIL}        |
| passDate                 | {RANDOM_DATE}         |
| maxResults               | {RANDOM_NUMBER:9}     |
| @header.JMSDestination   | queue                 |
| @header.JMSDeliveryMode  | 1                     |
| @header.JMSExpiration    | {RANDOM_NUMBER:2}     |
| @header.JMSPriority      | {RANDOM_NUMBER:1}     |
| @header.JMSMessageID     | {RANDOM_STRING:5}     |
| @header.JMSTimestamp     | {RANDOM_NUMBER:10}    |
| @header.JMSCorrelationID | correlate accordingly |
| @header.JMSReplyTo       | reply back            |
| @header.JMSType          | not my type           |
| @header.JMSRedelivered   | false                 |
When [NameRequest] is sent to destination [QUEUE] on JMS broker [TEST]
