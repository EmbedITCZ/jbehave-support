Given [TEST]/[action-test.html] url is open

When on [home] page these actions are performed:
| element               | action       | data  |
| #click-btn            | CLICK        |       |
| #double-click-btn     | DOUBLE_CLICK |       |
| #press-btn            | PRESS        | UP    |
| #clear-input          | CLEAR        |       |
| #fill-input           | FILL         | foo   |
| #select               | SELECT       | two   |
| #alert-accept-btn     | CLICK        |       |
| @alert                | ACCEPT       |       |
| #alert-dismiss-btn    | CLICK        |       |
| @alert                | DISMISS      |       |
| #checkbox-input-true  | SELECT       |       |
| #checkbox-input-false | SELECT       | false |

Then on [home] page these conditions are verified:
| element               | property      | data           |
| #click-result         | TEXT          | OK             |
| #double-click-result  | TEXT          | OK             |
| #press-result         | TEXT          | OK             |
| #clear-input          | VALUE         | {EMPTY_STRING} |
| #fill-input           | VALUE         | foo            |
| #select               | SELECTED_TEXT | two            |
| #alert-accept-result  | TEXT          | OK             |
| #alert-dismiss-result | TEXT          | OK             |
| #checkbox-input-true  | SELECTED      | true           |
| #checkbox-input-false | SELECTED      | false          |
