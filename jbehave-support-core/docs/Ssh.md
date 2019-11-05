[Contents](../README.md)

## Ssh steps

### Configuration

For each SSH connection used in the scenarios a bean of `SshTemplate` type with a ssh ID qualifier should be added to the Spring context.

Tool for creating sshTemplate `(SshSetting.getSshSettings)` can be used in the most simple scenarios. This helper combines provided values (hostnames, ports, users, passes, logFiles) based on their position in list, taking first item if relevant position is missing. This process is driven by hostnames, please see following examples for better understanding.

| hostnames  | ports | users   | passwords | logs      | output: SshSettings                                                                              |
|------------|-------|---------|-----------|-----------|--------------------------------------------------------------------------------------------------|
| wls1       | 22    | me      | pwd1      | file1     | ssh me@wls1 -p 22 //using pwd1, file1                                                            |
| wls1, wls2 | 22    | me      | pwd1      | file1     | ssh me@**wls1** -p 22 //using pwd1, file1 **AND** ssh me@**wls2** -p 22 //using pwd1, file1      |
| wls1       | 19,22 | you, me | myPW,pwd1 | log1.log  | **ONLY** ssh you@wls1 -p 19 //using myPW, log1.log                                               |
| wls1, wls2 | 22,23 | me      | pwd       | today.log | ssh me@**wls1 -p 22** //using pwd, today.log **AND** ssh me@**wls2 -p 23**//using pwd, today.log |

Behaviour presented on ports is the very same for users, passwords and logs.
If you have more specific needs not covered by this helper, feel free to combine properties into your own sshTemplate as you require.

Supported authentication methods are username with password or public key (optionally also with passphrase).
In case that both password and public key are set then the public key gets precedence and password is ignored.

Customization is allowed by overriding SshHandler and registering custom handler as a bean.

#### Limiting error count in logs

Ssh steps use soft assertions to report errors - by default only first 10 comparison errors are reported (to avoid out of memory problems with long logs).  
This number can be changed by setting the property `ssh.max.assert.count`.

### Usage of Ssh related steps:

(examples below use _TEST_ as ssh ID qualifier)  
The log can be searched for a presence of values using the following step: (_header_ is just the header of the table, not the value searched)
```
Then the following data are present in [TEST] log:
| header                                                          |
| :applicationIDHC>{CP:applicationIDHC}</                         |
| :buyback>{CP:BUYBACK}</                                         |
| :purchaseMPCartList>                                            |
```
If the pipe `|` character is needed to be matched some other table separator should be used for the table, eg.
```
Then the following data are present in [TEST] log:
{headerSeparator=!,valueSeparator=!}
! presentData                                       !
! n|salesPoint.numApprovedPOS|{CP:BSL_APPROVED_POS} !
```

Absence of values from log can also be checked in a similar fashion:
```
Then the following data are not present in [TEST] log:
| missingData               |
| salesPoint.numApprovedPOS |
```

Alternatively the steps above can also be used with specified verifier for each row (useful for matching by regular expressions):
```
Then the following data are present in [TEST] log:
| header     | verifier    |
| ^some.*$   | REGEX_MATCH |
| unexpected |             |
```
Note that only verifiers applicable for String make sense in this case - usage of other verifiers can cause unpredicted behavior.

#### Search in specific log part:
Ssh steps mentioned above by default fetch the log from whole run of the test story.  
Their behaviour can be modified by setting timestamps (it's highly recommended to set both timestamps for performance reasons/caching!)
```
Given log start timestamp is set to current time
Given log end timestamp is set to current time
```
Or saving timestamps to context and setting them later (if you want to check more separate parts of logs at the end of story)
```
Given current time is saved as log timestamp [NCAKO_1]
Given current time is saved as log timestamp [NCAKO_2]

Given log start timestamp is set to [NCAKO_1]
Given log end timestamp is set to [NCAKO_2]
```
Afterwards the same principle of searching the logs as described above applies
