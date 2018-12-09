/*
TODO: use proper queries, not IDs directly
*/

/* collections */
INSERT INTO collection (collection_ref, name, description)
VALUES ('2', 'Haslam', 'Photographs');

INSERT INTO collection (collection_ref, name, description)
VALUES ('3', 'Elliott', 'Photograph Album');

INSERT INTO collection (collection_ref, name, description)
VALUES ('1', 'Trotter', 'Photographic Work');

/* subcollections. All collections have an 'Uncategorized' subcollection. */
INSERT INTO subcollection (subcollection_ref, name, collectionId)
VALUES ('1', 'Uncategorized', 1);
INSERT INTO subcollection (subcollection_ref, name, collectionId)
VALUES ('1', 'Uncategorized', 2);
INSERT INTO subcollection (subcollection_ref, name, collectionId)
VALUES ('1', 'Uncategorized', 3);

/*
INSERT INTO subcollection (subcollection_ref, description, collectionId)
VALUES ('2', '972 b/w photographs...', 1);

INSERT INTO subcollection (subcollection_ref, description, collectionId)
VALUES ('3', '31 b/w photographs, mostly unmarked...', 1);

INSERT INTO subcollection (subcollection_ref, description, collectionId)
VALUES ('4', 'Photos of India and East Africa...', 2);

INSERT INTO subcollection (subcollection_ref, description, collectionId)
VALUES ('5', 'Photos of Persian Gulf...', 2);
*/

/* Items */

/*
INSERT INTO Item
(itemRef, location, name, description, dateCreated, copyrighted, extent, subcollectionId)
VALUES (
'2001/090/1/1/4689',
'Kenya',
'Uniformed Soldiers at a KAR camp',
'Uniformed...',
'June 1953',
1,
'1 Negative',
4);
*/

/*
--For reference

select Item.name from Item join subcollection using (id);

--ID #1 is Gerald, since he was added first.
INSERT INTO CommitteeRole (name, incumbent) VALUES ('Treasurer', 1);

INSERT INTO Event (name, date, location, description, organiser)
VALUES ('Shindig1', '2018-03-20 20:00:00', 'The Moon', 'Funtimes', 1);

--Note same date, but different location, so no problem.
INSERT INTO Event (name, date, location, description, organiser)
VALUES ('Shindig2', '2018-03-20 20:00:00', 'Cloud Nine', 'Dancing', 2);

--Gerald is attending Shindig2
INSERT INTO Attendance (member, event) VALUES (1, 2);
--So is Nancy
INSERT INTO Attendance (member, event) VALUES (2, 2);

--Get names of all members who are on the committee
--SELECT Member.name FROM Member JOIN CommitteeRole USING (id);

--Get IDs of all attendees of Shindig2
--SELECT member FROM Attendance JOIN Event ON Event.id = Attendance.event;

--This should get their names, but for some reason returns them twice. So it's
--finding the cross product when I don't want it to.

--SELECT Member.name FROM Member JOIN (SELECT member FROM Attendance
--JOIN Event ON Event.id = Attendance.event) AS guests;

--Get all events with more than 1 attendee?
*/
