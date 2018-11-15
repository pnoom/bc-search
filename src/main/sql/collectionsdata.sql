/*
TODO: use proper queries, not IDs directly
*/

/* Collections */
INSERT INTO Collection (name, description)
VALUES ('Trotter', 'Photographic Work');

INSERT INTO Collection (name, description)
VALUES ('Haslam', 'Photographs');

INSERT INTO Collection (name, description)
VALUES ('Elliott', 'Photograph Album');

/* SubCollections */
INSERT INTO SubCollection (description, collectionId)
VALUES ('972 b/w photographs...', 1);

INSERT INTO SubCollection (description, collectionId)
VALUES ('31 b/w photographs, mostly unmarked...', 1);

INSERT INTO SubCollection (description, collectionId)
VALUES ('Photos of India and East Africa...', 2);

INSERT INTO SubCollection (description, collectionId)
VALUES ('Photos of Persian Gulf...', 2);

/* Items */
INSERT INTO Item
(name, description, copyrighted, objectNumber, nature, collectionId)
VALUES (
'Uniformed Soldiers at a KAR camp',
'Uniformed...',
1,
'2001/090/1/1/4689',
'1 Negative',
1);

/*

select Item.name from Item join Collection using (id);


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
