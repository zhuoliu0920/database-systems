DROP TABLE IF EXISTS R;

SELECT "a","b"
INTO R
FROM graph
WHERE 1=2;

ALTER TABLE R
ADD COLUMN d int;

INSERT INTO R
SELECT DISTINCT "a","b",1
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
SELECT DISTINCT r1."a" as "a", t1."b" as "b", r1.d+1 as d
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
SELECT DISTINCT r1."a" as "a", t1."b" as "b", r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT DISTINCT r1."a" as "a", t1."b" as "b", r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT DISTINCT r1."a" as "a", t1."b" as "b", r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT DISTINCT r1."a" as "a", t1."b" as "b", r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT DISTINCT r1."a" as "a", t1."b" as "b", r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT DISTINCT r1."a" as "a", t1."b" as "b", r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT DISTINCT r1."a" as "a", t1."b" as "b", r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT DISTINCT r1."a" as "a", t1."b" as "b", r1.d+1 as d
FROM (
(SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"<>"b") AS r1
INNER JOIN Rbase AS t1
ON r1."b" = t1."a") AS tmp;

SELECT MAX(d)
FROM R;

DROP TABLE IF EXISTS O;

SELECT DISTINCT a , b , d INTO O FROM R
