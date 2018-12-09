
-- Default behaviour for simple search should be to check each word in a search term against item name, desc, location, and date.
-- If ANY of those match, return. This should be performed in a single query.

-- So a bit like below, only for every word in search term.

-- select item_ref, name, location, date_created, copyrighted, extent from item where name like '%search terms%' or description like '%search terms%';

select item_ref, name, location, date_created, copyrighted, extent
from item
where
item.name like '%search terms%' or
item.description like '%search terms%' or
item.location like '%search terms%' or
item.date_created like '%search terms%';

