DROP TABLE IF EXISTS R;

SELECT "a","b","value"
INTO R
FROM graph
WHERE 1=2;

ALTER TABLE R
ADD COLUMN p int;

ALTER TABLE R
ADD COLUMN d int;

INSERT INTO R
SELECT "a","b","value",1,1
FROM graph;

DROP TABLE IF EXISTS Rbase;

SELECT *
INTO Rbase
FROM R
WHERE 1=2;

INSERT INTO Rbase
SELECT *
FROM R
WHERE "a"<>"b";

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as "value", r1.p*t1.p as p, r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as "value", r1.p*t1.p as p, r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as "value", r1.p*t1.p as p, r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as "value", r1.p*t1.p as p, r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as "value", r1.p*t1.p as p, r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as "value", r1.p*t1.p as p, r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as "value", r1.p*t1.p as p, r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as "value", r1.p*t1.p as p, r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as "value", r1.p*t1.p as p, r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

SELECT MAX(d)
FROM R;

DROP TABLE IF EXISTS Q;

SELECT  * 
INTO Q
FROM (SELECT DISTINCT a , b  FROM R) as F; 

