Narrative:
In order to explain how web tables testing works
As a development team
I want to show you basic usage of web tables

Scenario: Web tables

!-- Open web browser on page from a configuration with suffix
Given [TEST]/[table.html] url is open

!-- Verify whole table
Then on [home] page the table [#roles-table] contains exactly the following data:
| Name     | Surname    |
| Thomas   | Anderson   |
| John     | Wick       |
| Johnny   | Silverhand |
| Theodore | Logan      |

!-- Verify specific row
Then on [home] page the table [#roles-table] contains in row 1 the following data:
| Name   | Surname  |
| Thomas | Anderson |

!-- Verify several specific rows next to each other
Then on [home] page the table [#roles-table] contains in rows 2 to 3 the following data:
| Name   | Surname    |
| John   | Wick       |
| Johnny | Silverhand |

!-- Verify that the data are somewhere in the table
Then on [home] page the table [#roles-table] contains the following data:
| Name | Surname |
| John | Wick    |

!-- Verify that just these specific data are somewhere in the table
Then on [home] page the table [#roles-table] contains all of the following data regardless of order:
| Name     | Surname    |
| Thomas   | Anderson   |
| John     | Wick       |
| Theodore | Logan      |
| Johnny   | Silverhand |
