Given [TEST]/[property-test.html] url is open

Then on [home] page these conditions are verified:
| element              | property      | data     |
| #enabled-btn         | ENABLED       | true     |
| #disabled-btn        | ENABLED       | false    |
| #selected-checkbox   | SELECTED      | true     |
| #unselected-checkbox | SELECTED      | false    |
| #text                | TEXT          | text     |
| #class               | CLASS         | my-class |
| #value               | VALUE         | value    |
| #value               | DISPLAYED     | true     |
| #xxx                 | DISPLAYED     | false    |
| #value               | EDITABLE      | true     |
| #readonly            | EDITABLE      | false    |
| #select              | SELECTED_TEXT | two      |
| #table               | ROW_COUNT     | 3        |
