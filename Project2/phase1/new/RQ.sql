DROP TABLE IF EXISTS T;

SELECT "from","to","distance"
INTO T
FROM flight
WHERE 1=2;

ALTER TABLE T
ADD COLUMN p int;

INSERT INTO T
SELECT "from","to",MIN("distance"),COUNT(*)
FROM flight
GROUP BY "from","to";

DROP TABLE IF EXISTS R;

SELECT *
INTO R
FROM T
WHERE 1=2;

ALTER TABLE R
ADD COLUMN d int;

INSERT INTO R
SELECT "from","to","distance",p,1
FROM T
WHERE "from" <> "to";

SELECT COUNT(*)
FROM (
SELECT "from" AS node FROM T
UNION
SELECT "to" AS node FROM T) AS tmp;

DROP TABLE IF EXISTS Laplacian;

SELECT "from" AS node FROM T
UNION
SELECT "to" AS node FROM T;

CREATE TABLE Laplacian (
"Houston" int,
"Beijing" int,
"Hongkong" int,
"Shanghai" int);

SELECT COUNT(*) FROM
(SELECT DISTINCT "from","to"
FROM T
WHERE "from"<>"to" AND "from"='Houston' OR (
"to"='Houston' AND "from" NOT IN (
SELECT "to" FROM T WHERE "from"='Houston'))) as tmp;

SELECT "from","to"
FROM T
WHERE ("from"='Houston' AND "to"='Beijing') OR ("to"='Houston' AND "from"='Beijing');

SELECT "from","to"
FROM T
WHERE ("from"='Houston' AND "to"='Hongkong') OR ("to"='Houston' AND "from"='Hongkong');

SELECT "from","to"
FROM T
WHERE ("from"='Houston' AND "to"='Shanghai') OR ("to"='Houston' AND "from"='Shanghai');

INSERT INTO Laplacian VALUES (2,-1,-1,0);

SELECT "from","to"
FROM T
WHERE ("from"='Beijing' AND "to"='Houston') OR ("to"='Beijing' AND "from"='Houston');

SELECT COUNT(*) FROM
(SELECT DISTINCT "from","to"
FROM T
WHERE "from"<>"to" AND "from"='Beijing' OR (
"to"='Beijing' AND "from" NOT IN (
SELECT "to" FROM T WHERE "from"='Beijing'))) as tmp;

SELECT "from","to"
FROM T
WHERE ("from"='Beijing' AND "to"='Hongkong') OR ("to"='Beijing' AND "from"='Hongkong');

SELECT "from","to"
FROM T
WHERE ("from"='Beijing' AND "to"='Shanghai') OR ("to"='Beijing' AND "from"='Shanghai');

INSERT INTO Laplacian VALUES (-1,2,0,-1);

SELECT "from","to"
FROM T
WHERE ("from"='Hongkong' AND "to"='Houston') OR ("to"='Hongkong' AND "from"='Houston');

SELECT "from","to"
FROM T
WHERE ("from"='Hongkong' AND "to"='Beijing') OR ("to"='Hongkong' AND "from"='Beijing');

SELECT COUNT(*) FROM
(SELECT DISTINCT "from","to"
FROM T
WHERE "from"<>"to" AND "from"='Hongkong' OR (
"to"='Hongkong' AND "from" NOT IN (
SELECT "to" FROM T WHERE "from"='Hongkong'))) as tmp;

SELECT "from","to"
FROM T
WHERE ("from"='Hongkong' AND "to"='Shanghai') OR ("to"='Hongkong' AND "from"='Shanghai');

INSERT INTO Laplacian VALUES (-1,0,2,-1);

SELECT "from","to"
FROM T
WHERE ("from"='Shanghai' AND "to"='Houston') OR ("to"='Shanghai' AND "from"='Houston');

SELECT "from","to"
FROM T
WHERE ("from"='Shanghai' AND "to"='Beijing') OR ("to"='Shanghai' AND "from"='Beijing');

SELECT "from","to"
FROM T
WHERE ("from"='Shanghai' AND "to"='Hongkong') OR ("to"='Shanghai' AND "from"='Hongkong');

SELECT COUNT(*) FROM
(SELECT DISTINCT "from","to"
FROM T
WHERE "from"<>"to" AND "from"='Shanghai' OR (
"to"='Shanghai' AND "from" NOT IN (
SELECT "to" FROM T WHERE "from"='Shanghai'))) as tmp;

INSERT INTO Laplacian VALUES (0,-1,-1,2);

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT "from","to",MIN("distance"),SUM(p),MIN(d)
FROM (
SELECT r1."from" as "from", t1."to" as "to", r1."distance"+t1."distance" as"distance", r1.p*t1.p as p, d+1 as d
FROM (
SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R)) AS r1
INNER JOIN T AS t1
ON r1."to" = t1."from") AS tmp
GROUP BY "from","to";

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "from"= "to";

DELETE FROM R
WHERE "from" = "to" OR ("from","to","distance") NOT IN (
SELECT "from","to",MIN("distance")
FROM R
GROUP BY "from","to");

DELETE FROM R
WHERE (d,"from","to") NOT IN (
SELECT MIN(d),"from","to"
FROM R
GROUP BY "from","to");

SELECT MAX(d)
FROM R;

