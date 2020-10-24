Narrative:
In order to explain how ServerLogReportExtension work
As a development team
I want to use different report modes in 3 stories
So you can se in output:
2X LONG_REPORTABLE (file) created from TEMPLATE and FULL mode
2X TEST created from CACHE and FULL mode

Scenario: Test TEMPLATE mode of ssh extension

Given ssh reporter mode is set to [TEMPLATE]
And ssh test data are filled

Then the following data are present in [TEST] log:
| header     |
| long       |
| unexpected |

Scenario: Test CACHE mode of ssh extension

Given ssh reporter mode is set to [CACHE]
And ssh test data are filled

Then the following data are present in [TEST] log:
| header     |
| long       |
| unexpected |

Scenario: Test FULL mode of ssh extension

Given ssh reporter mode is set to [FULL]
And ssh test data are filled

Then the following data are present in [TEST] log:
| header     |
| long       |
| unexpected |
