
-- Default behaviour for simple search should be to check each word in a search term against item name, desc, location, and date.
-- If ANY of those match, return. This should be performed in a single query.

-- So a bit like below, only for every word in search term.

-- select item_ref, name, location, date_created, copyrighted, extent from item where name like '%search terms%' or description like '%search terms%';

-- Want select distinct on one column only, so use group by instead

select item_ref, name, location, date_created, copyrighted, extent
from item
where id in (select id from item where
item.name like '%Kenya soldiers%' or
item.description like '%Kenya soldiers%' or
item.location like '%Kenya soldiers%' or
item.date_created like '%Kenya soldiers%'
group by item_ref);


select item_ref, name, location, date_created, copyrighted, extent
from item
where name in (select name from item where
item.name like '%Kenya soldiers%' or
item.description like '%Kenya soldiers%' or
item.location like '%Kenya soldiers%' or
item.date_created like '%Kenya soldiers%'
group by item_ref);

/*

The thing that will produce duplicates is the fact that we run a query for each
search word individually. So an item might match by more than word search word.
How to simulate this in pure SQL?

*/

-- For the search string "Kenya soldiers"

select item_ref, name, location
from item
where id in
(select min(id) from item where
item.name like '%Kenya%' or
item.description like '%Kenya%' or
item.location like '%Kenya%' or
item.date_created like '%Kenya%' or
item.name like '%soldiers%' or
item.description like '%soldiers%' or
item.location like '%soldiers%' or
item.date_created like '%soldiers%'
group by id);



select item_ref, name, location
from item
where id in
(select id from item where
item.name like '%Kenya%' or
item.description like '%Kenya%' or
item.location like '%Kenya%' or
item.date_created like '%Kenya%')
as i1
on item.id = i1.id
left join
(select id from item where
item.name like '%soldiers%' or
item.description like '%soldiers%' or
item.location like '%soldiers%' or
item.date_created like '%soldiers%')
as i2
on item.id = i2.id;

